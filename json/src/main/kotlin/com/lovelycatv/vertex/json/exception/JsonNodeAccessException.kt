package com.lovelycatv.vertex.json.exception

import com.lovelycatv.vertex.json.JSONNode

/**
 * @author lovelycat
 * @since 2025-08-01 01:12
 * @version 1.0
 */
class JsonNodeAccessException(
    node: JSONNode,
    accessTarget: Any,
    expected: Class<*>,
    actual: Class<*>?
) : RuntimeException("Could not access $accessTarget of ${node.toJSONString()}, expected: ${expected.canonicalName}, actual: ${actual?.canonicalName}")