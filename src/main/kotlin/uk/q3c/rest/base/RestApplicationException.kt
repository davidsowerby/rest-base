package uk.q3c.rest.base

/**
 * Created by David Sowerby on 14 Feb 2017
 */
class RestApplicationException(val msg: String, val exception: Exception? = null) : RuntimeException(msg, exception)