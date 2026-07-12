package org.ferris.sushi.endpoint.event.consumer.model;

import org.ferris.sushi.PojaGenerated;
import org.ferris.sushi.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
