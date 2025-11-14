package ape.nexitool.contracts;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Transform {
  public void execute(ObjectNode root);
}
