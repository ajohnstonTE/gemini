package com.techempower.gemini.configuration2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class VariableReplacementConfigurationLoader
    implements ConfigurationLoader
{
  private static final VariableReferenceParser PARSER = new VariableReferenceParser();

  @Override
  public ObjectNode load(ObjectNode root) throws Exception
  {
    replaceVariablesInChildren(root, root);
    replaceDoubleDollarSigns(root, null);
    return root;
  }

  private void replaceDoubleDollarSigns(JsonNode node,
                                        Consumer<JsonNode> replacer)
  {
    if (node.isTextual())
    {
      replacer.accept(new TextNode(
          node.textValue().replaceAll("\\${2}", "\\$")));
    }
    else if (node.isArray())
    {
      ArrayNode parent = (ArrayNode) node;
      int index = 0;
      for (JsonNode child : node)
      {
        int indexToSet = index;
        replaceDoubleDollarSigns(child,
            replacement -> parent.set(indexToSet, replacement));
        index++;
      }
    }
    else if (node.isObject())
    {
      ObjectNode parent = (ObjectNode) node;
      Iterator<Map.Entry<String, JsonNode>> fields = parent.fields();
      while (fields.hasNext())
      {
        Map.Entry<String, JsonNode> field = fields.next();
        String fieldName = field.getKey();
        JsonNode child = field.getValue();
        replaceDoubleDollarSigns(child,
            replacement -> parent.set(fieldName, replacement));
      }
    }
  }

  private void replaceVariablesInChildren(ObjectNode root,
                                          JsonNode parent) throws Exception
  {
    if (parent.isObject())
    {
      Iterator<Map.Entry<String, JsonNode>> fields = parent.fields();
      while (fields.hasNext())
      {
        Map.Entry<String, JsonNode> field = fields.next();
        String fieldName = field.getKey();
        if (parent == root && fieldName.equals("env"))
        {
          // Do not do replacement for environment values.
          continue;
        }
        JsonNode fieldNode = field.getValue();
        if (fieldNode.isContainerNode())
        {
          replaceVariablesInChildren(root, fieldNode);
        }
        else if (fieldNode.isTextual())
        {
          ObjectNode parentObjNode = (ObjectNode) parent;
          replaceReferences(root, (TextNode) fieldNode,
              node -> parentObjNode.set(fieldName, node));
        }
      }
    }
    else if (parent.isArray())
    {
      int index = 0;
      for (JsonNode fieldNode : parent)
      {
        if (fieldNode.isContainerNode())
        {
          replaceVariablesInChildren(root, fieldNode);
        }
        else if (fieldNode.isTextual())
        {
          ArrayNode parentArrNode = (ArrayNode) parent;
          int indexToSet = index;
          replaceReferences(root, (TextNode) fieldNode,
              node -> parentArrNode.set(indexToSet, node));
        }
        index++;
      }
    }
    else
    {
      throw new Exception("Unsupported parent JsonNode detected while " +
          "replacing variables");
    }
  }

  private void replaceReferences(ObjectNode root,
                                 TextNode textNode,
                                 Consumer<JsonNode> replacer)
  {
    Set<String> pathsSeen = new HashSet<>();
    List<Segment> segments = PARSER.parse(textNode.textValue());
    if (segments.size() == 1 && segments.get(0).isReference())
    {
      ReferenceSegment referenceSegment = ((ReferenceSegment) segments.get(0));
      if (referenceSegment.getDollarSignsPrefix().isEmpty())
      {
        String path = referenceSegment.getPath();
        PropertyAccess propertyAccess = PropertyAccess.fromPath(path);
        JsonNode node = propertyAccess.read(root);
        if (node == null || !node.isTextual())
        {
          replacer.accept(node);
          return;
        }
      }
    }
    String newStr = segments.stream()
        .map(segment -> replaceStringReferences(root, segment, Set.of()))
        .collect(Collectors.joining(""));
    replacer.accept(new TextNode(newStr));
  }

  private String replaceStringReferences(ObjectNode root,
                                         Segment segment,
                                         Set<String> pathsSeen)
  {
    if (segment.isText())
    {
      return ((TextSegment) segment).getText();
    }
    ReferenceSegment referenceSegment = (ReferenceSegment) segment;
    Set<String> localPathsSeen = new HashSet<>(pathsSeen);
    String prefix = referenceSegment.getDollarSignsPrefix();
    String path = referenceSegment.getPath();
    if (!localPathsSeen.add(path))
    {
      throw new CircularVariableReferenceException();
    }
    PropertyAccess propertyAccess = PropertyAccess.fromPath(path);
    JsonNode node = propertyAccess.read(root);
    if (node == null || node.isNull())
    {
      return prefix;
    }
    else if (node.isContainerNode())
    {
      throw new RuntimeException("Cannot stringify an object/array for reference");
    }
    else
    {
      String text = node.asText();
      List<Segment> segments = PARSER.parse(text);
      return prefix + segments.stream()
          .map(innerSegment -> replaceStringReferences(root, innerSegment,
              localPathsSeen))
          .collect(Collectors.joining(""));
    }
  }
}
