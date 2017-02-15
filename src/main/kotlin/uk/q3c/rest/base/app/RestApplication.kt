package uk.q3c.rest.base.app

import com.google.common.collect.ImmutableList
import com.google.inject.Module
import uk.q3c.rest.base.resource.SampleModule

/**
 * Created by David Sowerby on 14 Feb 2017
 */
interface RestApplication {
    fun modules(): List<Module>
}

class SampleRestApplication : RestApplication {
    override fun modules(): List<Module> {
        return ImmutableList.of(SampleModule())
    }

}
