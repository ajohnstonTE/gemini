package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class PropertyAccess
{
  private static final Pattern ROOT_FIELD_ACCESS   = Pattern.compile("^\\w+");
  private static final Pattern FIELD_ACCESS        = Pattern.compile("^\\.(\\w+)");
  private static final Pattern ARRAY_ACCESS_OPEN   = Pattern.compile("^\\[");
  private static final Pattern ARRAY_ACCESS_END    = Pattern.compile("^]");
  private static final Pattern ARRAY_ACCESS_NUMBER = Pattern.compile("^\\d+");

  private final PropertyAccess parent;

  public PropertyAccess(PropertyAccess parent)
  {
    this.parent = parent;
  }

  PropertyAccess getParent()
  {
    return parent;
  }

  abstract JsonNode read(JsonNode node);

  abstract void write(JsonNode node, JsonNode value);

  void write(ObjectNode root, String value) throws JsonProcessingException
  {
    write(root, new YamlValueParser().getValueFromString(value));
  }

  abstract JsonNode init(JsonNode node, JsonNode defaultValue);

  public static PropertyAccess fromPath(String path)
  {
    StringBuilder pathSoFar = new StringBuilder();
    String currentPath = path;
    Matcher matcher = ROOT_FIELD_ACCESS.matcher(currentPath);
    if (!matcher.find())
    {
      throw new RuntimeException("Attempting to set non-field-based root "
          + "value. Encountered error at: " + pathSoFar.toString() + "^"
          + currentPath);
    }
    pathSoFar.append(currentPath, 0, matcher.end());
    currentPath = currentPath.substring(matcher.end());
    PropertyAccess currentAccess = new FieldAccess(null, matcher.group());
    while (!currentPath.isEmpty())
    {
      matcher = FIELD_ACCESS.matcher(currentPath);
      if (matcher.find())
      {
        pathSoFar.append(currentPath, 0, matcher.end());
        currentPath = currentPath.substring(matcher.end());
        currentAccess = new FieldAccess(currentAccess, matcher.group(1));
        continue;
      }
      matcher = ARRAY_ACCESS_OPEN.matcher(currentPath);
      if (matcher.find())
      {
        pathSoFar.append(currentPath, 0, matcher.end());
        currentPath = currentPath.substring(matcher.end());
        matcher = ARRAY_ACCESS_NUMBER.matcher(currentPath);
        if (!matcher.find())
        {
          throw new RuntimeException("Integer not detected while reading array " +
              "access index. Encountered error at: " + pathSoFar.toString() + "^"
              + currentPath);
        }
        pathSoFar.append(currentPath, 0, matcher.end());
        currentPath = currentPath.substring(matcher.end());
        String nextIndexStr = matcher.group();
        int index = Integer.parseInt(nextIndexStr);
        matcher = ARRAY_ACCESS_END.matcher(currentPath);
        if (!matcher.find())
        {
          throw new RuntimeException("End of array access not detected. Encountered " +
              "error at: " + pathSoFar.toString() + "^"
              + currentPath);
        }
        pathSoFar.append(currentPath, 0, matcher.end());
        currentPath = currentPath.substring(matcher.end());
        currentAccess = new ArrayAccess(currentAccess, index);
        continue;
      }
      throw new RuntimeException("Could not fully parse node path. " +
          "Encountered error at: " + pathSoFar.toString() + "^"
          + currentPath);
    }
    return currentAccess;
  }

  public static class ArrayAccess
      extends PropertyAccess
  {
    private final int index;

    public ArrayAccess(PropertyAccess parent, int index)
    {
      super(parent);
      this.index = index;
    }

    @Override
    JsonNode read(JsonNode node)
    {
      PropertyAccess parent = getParent();
      if (parent != null)
      {
        node = parent.read(node);
      }
      if (node == null || node.isNull())
      {
        return null;
      }
      if (!node.isArray())
      {
        throw new RuntimeException("Cannot read by index from non-array");
      }
      return node.get(index);
    }

    @Override
    void write(JsonNode node, JsonNode value)
    {
      PropertyAccess parent = getParent();
      if (parent != null)
      {
        node = parent.init(node, new ObjectMapper().createArrayNode());
      }
      ArrayNode arrayNode = (ArrayNode) node;
      int size = arrayNode.size();
      if (size <= index)
      {
        for (int i = 0; i < index - size + 1; i++)
        {
          arrayNode.addNull();
        }
      }
      arrayNode.set(index, value);
    }

    @Override
    JsonNode init(JsonNode node, JsonNode defaultValue)
    {
      PropertyAccess parent = getParent();
      if (parent != null)
      {
        node = parent.init(node, new ObjectMapper().createArrayNode());
      }
      ArrayNode arrayNode = (ArrayNode) node;
      JsonNode currentValue = arrayNode.get(index);
      int size = arrayNode.size();
      if (size <= index)
      {
        for (int i = 0; i < index - size + 1; i++)
        {
          arrayNode.addNull();
        }
      }
      if (currentValue == null
          || currentValue.getNodeType() != defaultValue.getNodeType())
      {
        arrayNode.set(index, defaultValue);
      }
      return arrayNode.get(index);
    }

    @Override
    public boolean equals(Object o)
    {
      if (this == o) return true;
      if (!(o instanceof ArrayAccess)) return false;
      ArrayAccess that = (ArrayAccess) o;
      return index == that.index
          && Objects.equals(getParent(), that.getParent());
    }

    @Override
    public int hashCode()
    {
      return Objects.hash(index, getParent());
    }
  }

  public static class FieldAccess
      extends PropertyAccess
  {
    private final String field;

    public FieldAccess(PropertyAccess parent, String field)
    {
      super(parent);
      this.field = field;
    }

    @Override
    JsonNode read(JsonNode node)
    {
      PropertyAccess parent = getParent();
      if (parent != null)
      {
        node = parent.read(node);
      }
      if (node == null || node.isNull())
      {
        return null;
      }
      if (!node.isObject())
      {
        throw new RuntimeException("Cannot read field of non-object");
      }
      return node.get(field);
    }

    @Override
    void write(JsonNode node, JsonNode value)
    {
      PropertyAccess parent = getParent();
      if (parent != null)
      {
        node = parent.init(node, new ObjectMapper().createObjectNode());
      }
      ObjectNode objectNode = (ObjectNode) node;
      objectNode.set(field, value);
    }

    @Override
    JsonNode init(JsonNode node, JsonNode defaultValue)
    {
      PropertyAccess parent = getParent();
      if (parent != null)
      {
        node = parent.init(node, new ObjectMapper().createObjectNode());
      }
      ObjectNode objectNode = (ObjectNode) node;
      JsonNode currentValue = objectNode.get(field);
      if (currentValue == null
          || currentValue.getNodeType() != defaultValue.getNodeType())
      {
        objectNode.set(field, defaultValue);
      }
      return objectNode.get(field);
    }

    @Override
    public boolean equals(Object o)
    {
      if (this == o) return true;
      if (!(o instanceof FieldAccess)) return false;
      FieldAccess that = (FieldAccess) o;
      return Objects.equals(field, that.field)
          && Objects.equals(getParent(), that.getParent());
    }

    @Override
    public int hashCode()
    {
      return Objects.hash(field, getParent());
    }
  }
}
