package io.bootify.helpcenter.help

import org.springframework.stereotype.Component

interface MetricService {
    fun incrementGetHelpResource(nameWithType: String)
}

@Component
class NoopMetricService : MetricService {
    override fun incrementGetHelpResource(nameWithType: String) { /* no-op */ }
}
