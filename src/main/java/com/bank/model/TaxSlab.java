package com.bank.model;

public enum TaxSlab {
    // Old Regime Tax Slabs (FY 2024-25)
    SLAB_0_250000,      // 0% (Up to 2.5 lakh)
    SLAB_250000_500000, // 5% (2.5L - 5L)
    SLAB_500000_1000000,// 20% (5L - 10L)
    SLAB_ABOVE_1000000, // 30% (Above 10L)
    
    // New Regime Tax Slabs (FY 2024-25)
    NEW_SLAB_0_300000,     // 0% (Up to 3 lakh)
    NEW_SLAB_300000_600000,// 5% (3L - 6L)
    NEW_SLAB_600000_900000,// 10% (6L - 9L)
    NEW_SLAB_900000_1200000,// 15% (9L - 12L)
    NEW_SLAB_1200000_1500000,// 20% (12L - 15L)
    NEW_SLAB_ABOVE_1500000  // 30% (Above 15L)
}
