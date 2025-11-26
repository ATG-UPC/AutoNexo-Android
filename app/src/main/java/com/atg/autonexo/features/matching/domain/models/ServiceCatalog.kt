package com.atg.autonexo.features.matching.domain.models

enum class ServiceCatalog(val displayName: String) {

    // === MAINTENANCE ===
    OIL_CHANGE("Cambio de aceite"),
    OIL_FILTER_CHANGE("Cambio de filtro de aceite"),
    AIR_FILTER_CHANGE("Cambio de filtro de aire"),
    FUEL_FILTER_CHANGE("Cambio de filtro de combustible"),
    CABIN_FILTER_CHANGE("Cambio de filtro de habitáculo"),
    GENERAL_INSPECTION("Revisión general"),
    PREVENTIVE_MAINTENANCE("Mantenimiento preventivo"),

    // === BRAKES ===
    BRAKE_PAD_REPLACEMENT("Cambio de pastillas de freno"),
    BRAKE_DISC_REPLACEMENT("Cambio de discos de freno"),
    BRAKE_FLUID_CHANGE("Cambio de líquido de frenos"),
    BRAKE_SYSTEM_INSPECTION("Inspección de frenos"),

    // === ENGINE ===
    ENGINE_DIAGNOSTICS("Diagnóstico de motor"),
    SPARK_PLUG_REPLACEMENT("Cambio de bujías"),
    TIMING_BELT_REPLACEMENT("Cambio de correa de distribución"),
    INJECTOR_CLEANING("Limpieza de inyectores"),
    ENGINE_TUNEUP("Afinamiento de motor"),
    ENGINE_OVERHAUL("Rectificación de motor"),

    // === TRANSMISSION ===
    TRANSMISSION_OIL_CHANGE("Cambio de aceite de transmisión"),
    CLUTCH_REPLACEMENT("Cambio de embrague"),
    TRANSMISSION_DIAGNOSTICS("Diagnóstico de transmisión"),

    // === SUSPENSION ===
    WHEEL_ALIGNMENT("Alineación"),
    WHEEL_BALANCING("Balanceo"),
    SHOCK_ABSORBER_REPLACEMENT("Cambio de amortiguadores"),
    SUSPENSION_INSPECTION("Inspección de suspensión"),

    // === ELECTRICAL ===
    BATTERY_REPLACEMENT("Cambio de batería"),
    ALTERNATOR_REPAIR("Reparación de alternador"),
    STARTER_REPAIR("Reparación de motor de arranque"),
    ELECTRICAL_DIAGNOSTICS("Diagnóstico eléctrico"),
    HEADLIGHT_RESTORATION("Restauración de faros"),

    // === COOLING ===
    COOLANT_CHANGE("Cambio de refrigerante"),
    RADIATOR_REPAIR("Reparación de radiador"),
    THERMOSTAT_REPLACEMENT("Cambio de termostato"),
    WATER_PUMP_REPLACEMENT("Cambio de bomba de agua"),

    // === EXHAUST ===
    EXHAUST_REPAIR("Reparación de escape"),
    MUFFLER_REPLACEMENT("Cambio de silenciador"),
    CATALYTIC_CONVERTER_REPLACEMENT("Cambio de convertidor catalítico"),

    // === TIRES ===
    TIRE_CHANGE("Cambio de neumáticos"),
    TIRE_ROTATION("Rotación de neumáticos"),
    TIRE_REPAIR("Reparación de neumático"),

    // === BODYWORK ===
    DENT_REPAIR("Reparación de abolladuras"),
    PAINT_JOB("Pintura"),
    SCRATCH_REPAIR("Reparación de rayones"),

    // === DIAGNOSTICS ===
    ELECTRONIC_SCAN("Escaneo electrónico"),
    CHECK_ENGINE_DIAGNOSTICS("Diagnóstico check engine"),
    PRE_PURCHASE_INSPECTION("Inspección pre-compra"),

    // === OTHER ===
    AC_SERVICE("Servicio de aire acondicionado"),
    GLASS_REPLACEMENT("Cambio de vidrios"),
    DETAILING("Detailing"),
    CUSTOM_SERVICE("Servicio personalizado")
}
