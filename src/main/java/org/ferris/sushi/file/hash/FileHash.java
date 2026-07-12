package org.ferris.sushi.file.hash;

import org.ferris.sushi.PojaGenerated;

@PojaGenerated
public record FileHash(FileHashAlgorithm algorithm, String value) {}
