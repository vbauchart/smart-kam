package com.smartkam.project;

import java.util.List;

public record SheetRow(ModificationSheet sheet, SheetImpact impact, List<RefApply> applications) {}
