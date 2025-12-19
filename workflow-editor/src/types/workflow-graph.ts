import {randomUUID} from "@/utils/uuid.ts";
import {
  JVM_BOOLEAN_PRIMITIVE,
  JVM_BYTE_PRIMITIVE, JVM_CHAR_PRIMITIVE, JVM_DOUBLE_PRIMITIVE,
  JVM_FLOAT_PRIMITIVE,
  JVM_INT_PRIMITIVE, JVM_LONG_PRIMITIVE, JVM_NUMBER,
  JVM_SHORT_PRIMITIVE
} from "@/types/jvm.ts";

export type GraphNodeType =
  'ENTRY'
  | 'EXIT'
  | 'IF'
  | 'ADD'
  | 'SUB'
  | 'MUL'
  | 'DIV'
  | 'NUMBER_COMPARATOR'
  | 'STRING_CONTAINS'
  ;

export const DEFAULT_EDGE_GROUP = 'DEFAULT_EDGE_GROUP';

export interface WorkFlowGraphSerialization {
  graphNodeMap: Record<string, GraphNode>,
  graphNodeTriggerEdges: {
    groupId: string;
    from: string;
    to: string;
  }[],
  graphNodeParameterTransmissionEdges: {
    from: string;
    to: string;
    fromParameterName: string;
    toParameterName: string;
  }[],
  graphNodePositions: Record<string, [number, number]>
}

export const emptyWorkFlowGraphSerialization = () => {
  const entry = {
    ...emptyGraphNode("ENTRY", randomUUID(), "ENTRY"),
    strict: true
  }
  const exit = {
    ...emptyGraphNode("EXIT", randomUUID(), "EXIT"),
    strict: true
  }

  return {
    graphNodeMap: Object.fromEntries(
      [entry, exit].map((node) => [node.nodeId, node])
    ),
    graphNodeTriggerEdges: [],
    graphNodeParameterTransmissionEdges: [],
    graphNodePositions: Object.fromEntries(
      [entry, exit].map((node, index) => [node.nodeId, [400 * index, 0]])
    )
  } as WorkFlowGraphSerialization
}

export interface GraphNode {
  nodeType: GraphNodeType;
  nodeId: string;
  nodeName: string;
  inputs: { type: string; name: string }[];
  outputs: { type: string; name: string }[];
  [key: string]: unknown;
}

export type GraphNodeAttributes<T extends GraphNode> = Omit<T, 'nodeType' | 'nodeId' | 'nodeName' | 'inputs' | 'outputs'>;

export const emptyGraphNode = (nodeType: GraphNodeType, nodeId: string, nodeName: string) => {
  return {
    nodeType: nodeType,
    nodeId: nodeId,
    nodeName: nodeName,
    inputs: [],
    outputs: [],
  }
}

export interface GraphNodeEntry extends GraphNode {
  strict: boolean;
}

export interface GraphNodeExit extends GraphNode {
  strict: boolean;
}

export interface GraphNodeIf extends GraphNode {
}

export const WORKFLOW_GRAPH_SUPPORTS_DATA_TYPES = [
  JVM_BOOLEAN_PRIMITIVE,
  JVM_BYTE_PRIMITIVE,
  JVM_SHORT_PRIMITIVE,
  JVM_INT_PRIMITIVE,
  JVM_LONG_PRIMITIVE,
  JVM_FLOAT_PRIMITIVE,
  JVM_DOUBLE_PRIMITIVE,
  JVM_CHAR_PRIMITIVE,
  JVM_NUMBER
] as const;

export const MOCK_GRAPH_JSON: WorkFlowGraphSerialization = {
  "graphNodeMap": {
    "6f2e2872-e117-4f43-a947-9d9d7ccd0353": {
      "nodeType": "ENTRY",
      "nodeId": "6f2e2872-e117-4f43-a947-9d9d7ccd0353",
      "nodeName": "ENTRY",
      "inputs": [
        {
          "type": "int",
          "name": "x"
        },
        {
          "type": "int",
          "name": "y"
        }
      ],
      "outputs": [
        {
          "type": "int",
          "name": "x"
        },
        {
          "type": "int",
          "name": "y"
        }
      ],
      "strict": "true"
    },
    "9e83639b-5d8e-46e1-92cc-afcebb2a7083": {
      "nodeType": "ADD",
      "nodeId": "9e83639b-5d8e-46e1-92cc-afcebb2a7083",
      "nodeName": "ADD",
      "inputs": [
        {
          "type": "java.lang.Number",
          "name": "x"
        },
        {
          "type": "java.lang.Number",
          "name": "y"
        }
      ],
      "outputs": [
        {
          "type": "java.lang.Number",
          "name": "z"
        }
      ]
    },
    "6c43d506-d635-48df-9694-c557f11f65d7": {
      "nodeType": "NUMBER_COMPARATOR",
      "nodeId": "6c43d506-d635-48df-9694-c557f11f65d7",
      "nodeName": "NUMBER_COMPARATOR",
      "inputs": [
        {
          "type": "java.lang.Number",
          "name": "x"
        },
        {
          "type": "java.lang.Number",
          "name": "y"
        }
      ],
      "outputs": [
        {
          "type": "boolean",
          "name": "z"
        }
      ],
      "type": "GT"
    },
    "3966299a-7aed-43c7-9956-dd81582598ca": {
      "nodeType": "IF",
      "nodeId": "3966299a-7aed-43c7-9956-dd81582598ca",
      "nodeName": "IF",
      "inputs": [
        {
          "type": "boolean",
          "name": "condition"
        }
      ],
      "outputs": [

      ]
    },
    "e49cf5fb-02aa-4337-b5a7-7f40215fb56d": {
      "nodeType": "MUL",
      "nodeId": "e49cf5fb-02aa-4337-b5a7-7f40215fb56d",
      "nodeName": "MUL",
      "inputs": [
        {
          "type": "java.lang.Number",
          "name": "x"
        },
        {
          "type": "java.lang.Number",
          "name": "y"
        }
      ],
      "outputs": [
        {
          "type": "java.lang.Number",
          "name": "z"
        }
      ]
    },
    "8d802fe4-9298-44c5-a8ba-8dc73bc3b64b": {
      "nodeType": "DIV",
      "nodeId": "8d802fe4-9298-44c5-a8ba-8dc73bc3b64b",
      "nodeName": "DIV",
      "inputs": [
        {
          "type": "java.lang.Number",
          "name": "x"
        },
        {
          "type": "java.lang.Number",
          "name": "y"
        }
      ],
      "outputs": [
        {
          "type": "java.lang.Number",
          "name": "z"
        }
      ]
    },
    "ec8aa330-626e-47f6-8f7d-1fbed0ddfa84": {
      "nodeType": "EXIT",
      "nodeId": "ec8aa330-626e-47f6-8f7d-1fbed0ddfa84",
      "nodeName": "EXIT",
      "inputs": [
        {
          "type": "java.lang.Number",
          "name": "output"
        }
      ],
      "outputs": [
        {
          "type": "java.lang.Number",
          "name": "output"
        }
      ],
      "outputValueType": "java.lang.Number",
      "strict": "true"
    },
    "38d98798-7f1a-4f87-9afc-e367ce83b511": {
      "nodeType": "EXIT",
      "nodeId": "38d98798-7f1a-4f87-9afc-e367ce83b511",
      "nodeName": "EXIT",
      "inputs": [
        {
          "type": "java.lang.Number",
          "name": "output"
        }
      ],
      "outputs": [
        {
          "type": "java.lang.Number",
          "name": "output"
        }
      ],
      "outputValueType": "java.lang.Number",
      "strict": "true"
    }
  },
  "graphNodeTriggerEdges": [
    {
      "groupId": "DEFAULT_EDGE_GROUP",
      "from": "6f2e2872-e117-4f43-a947-9d9d7ccd0353",
      "to": "9e83639b-5d8e-46e1-92cc-afcebb2a7083"
    },
    {
      "groupId": "DEFAULT_EDGE_GROUP",
      "from": "9e83639b-5d8e-46e1-92cc-afcebb2a7083",
      "to": "6c43d506-d635-48df-9694-c557f11f65d7"
    },
    {
      "groupId": "DEFAULT_EDGE_GROUP",
      "from": "6c43d506-d635-48df-9694-c557f11f65d7",
      "to": "3966299a-7aed-43c7-9956-dd81582598ca"
    },
    {
      "groupId": "True",
      "from": "3966299a-7aed-43c7-9956-dd81582598ca",
      "to": "e49cf5fb-02aa-4337-b5a7-7f40215fb56d"
    },
    {
      "groupId": "False",
      "from": "3966299a-7aed-43c7-9956-dd81582598ca",
      "to": "8d802fe4-9298-44c5-a8ba-8dc73bc3b64b"
    },
    {
      "groupId": "DEFAULT_EDGE_GROUP",
      "from": "e49cf5fb-02aa-4337-b5a7-7f40215fb56d",
      "to": "ec8aa330-626e-47f6-8f7d-1fbed0ddfa84"
    },
    {
      "groupId": "DEFAULT_EDGE_GROUP",
      "from": "8d802fe4-9298-44c5-a8ba-8dc73bc3b64b",
      "to": "38d98798-7f1a-4f87-9afc-e367ce83b511"
    }
  ],
  "graphNodeParameterTransmissionEdges": [
    {
      "fromParameterName": "x",
      "toParameterName": "x",
      "from": "6f2e2872-e117-4f43-a947-9d9d7ccd0353",
      "to": "9e83639b-5d8e-46e1-92cc-afcebb2a7083"
    },
    {
      "fromParameterName": "y",
      "toParameterName": "y",
      "from": "6f2e2872-e117-4f43-a947-9d9d7ccd0353",
      "to": "9e83639b-5d8e-46e1-92cc-afcebb2a7083"
    },
    {
      "fromParameterName": "z",
      "toParameterName": "x",
      "from": "9e83639b-5d8e-46e1-92cc-afcebb2a7083",
      "to": "6c43d506-d635-48df-9694-c557f11f65d7"
    },
    {
      "fromParameterName": "x",
      "toParameterName": "y",
      "from": "6f2e2872-e117-4f43-a947-9d9d7ccd0353",
      "to": "6c43d506-d635-48df-9694-c557f11f65d7"
    },
    {
      "fromParameterName": "z",
      "toParameterName": "condition",
      "from": "6c43d506-d635-48df-9694-c557f11f65d7",
      "to": "3966299a-7aed-43c7-9956-dd81582598ca"
    },
    {
      "fromParameterName": "x",
      "toParameterName": "x",
      "from": "6f2e2872-e117-4f43-a947-9d9d7ccd0353",
      "to": "e49cf5fb-02aa-4337-b5a7-7f40215fb56d"
    },
    {
      "fromParameterName": "y",
      "toParameterName": "y",
      "from": "6f2e2872-e117-4f43-a947-9d9d7ccd0353",
      "to": "e49cf5fb-02aa-4337-b5a7-7f40215fb56d"
    },
    {
      "fromParameterName": "x",
      "toParameterName": "x",
      "from": "6f2e2872-e117-4f43-a947-9d9d7ccd0353",
      "to": "8d802fe4-9298-44c5-a8ba-8dc73bc3b64b"
    },
    {
      "fromParameterName": "y",
      "toParameterName": "y",
      "from": "6f2e2872-e117-4f43-a947-9d9d7ccd0353",
      "to": "8d802fe4-9298-44c5-a8ba-8dc73bc3b64b"
    },
    {
      "fromParameterName": "z",
      "toParameterName": "output",
      "from": "e49cf5fb-02aa-4337-b5a7-7f40215fb56d",
      "to": "ec8aa330-626e-47f6-8f7d-1fbed0ddfa84"
    },
    {
      "fromParameterName": "z",
      "toParameterName": "output",
      "from": "8d802fe4-9298-44c5-a8ba-8dc73bc3b64b",
      "to": "38d98798-7f1a-4f87-9afc-e367ce83b511"
    }
  ],
  "graphNodePositions": {}
}