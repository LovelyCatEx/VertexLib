package com.lovelycatv.vertex.json.exception

import com.lovelycatv.vertex.json.JSONNode

/**
 * @author lovelycat
 * @since 2025-08-01 01:14
 * @version 1.0
 */
class JsonIndexOutOfBoundException(
    node: JSONNode,
    tryIndex: Int
) : RuntimeException("The size of the object: ${node.toJSONString()} is ${node.elementSize}, trying access: $tryIndex")