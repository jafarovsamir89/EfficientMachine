package az.simplesoft.efficientmachine.simulation

data class Contract(
    val id: Int,
    val title: String,
    val description: String,
    val targetOutput: Double? = null,
    val targetFlow: Double? = null,
    val targetAverageValue: Double? = null,
    val maxPower: Int? = null,
    val requiredModule: ModuleType? = null,
) {
    fun progress(metrics: MachineMetrics): Double {
        val ratios = buildList {
            targetOutput?.let { add(metrics.outputPerSecond / it) }
            targetFlow?.let { add(metrics.flowPerSecond / it) }
            targetAverageValue?.let { add(metrics.averageValue / it) }
            maxPower?.let { add(if (metrics.powerUsed <= it) 1.0 else it.toDouble() / metrics.powerUsed) }
        }
        return (ratios.minOrNull() ?: 0.0).coerceIn(0.0, 1.0)
    }

    fun isSatisfied(metrics: MachineMetrics, modules: List<PlacedModule>): Boolean {
        if (targetOutput != null && metrics.outputPerSecond + 1e-6 < targetOutput) return false
        if (targetFlow != null && metrics.flowPerSecond + 1e-6 < targetFlow) return false
        if (targetAverageValue != null && metrics.averageValue + 1e-6 < targetAverageValue) return false
        if (maxPower != null && metrics.powerUsed > maxPower) return false
        if (requiredModule != null && modules.none { it.type == requiredModule }) return false
        return true
    }
}

object AlphaContracts {
    val all: List<Contract> = listOf(
        Contract(1, "First output", "Reach 2.5 output/sec", targetOutput = 2.5),
        Contract(2, "Make it valuable", "Reach 3.0 output/sec with ×2", targetOutput = 3.0, requiredModule = ModuleType.MULTIPLIER),
        Contract(3, "More flow", "Reach 2.5 flow/sec", targetFlow = 2.5),
        Contract(4, "Efficient build", "Reach 3.5 output/sec using at most 14 power", targetOutput = 3.5, maxPower = 14),
        Contract(5, "Split decision", "Use SPLIT and reach 3.5 output/sec", targetOutput = 3.5, requiredModule = ModuleType.SPLITTER),
        Contract(6, "High value", "Average delivered value ≥ 1.8", targetAverageValue = 1.8),
        Contract(7, "Compact power", "Reach 4.0 output/sec using at most 18 power", targetOutput = 4.0, maxPower = 18),
        Contract(8, "Fast factory", "Reach 3.0 flow/sec", targetFlow = 3.0),
        Contract(9, "Balanced machine", "Reach 4.5 output/sec and 2.5 flow/sec", targetOutput = 4.5, targetFlow = 2.5),
        Contract(10, "Alpha master", "Reach 5.0 output/sec under the 28 power cap", targetOutput = 5.0, maxPower = 28),
    )
}
