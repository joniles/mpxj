package org.mpxj.pwa;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.util.Map;

class MapRowDeserializer extends JsonDeserializer<Map<?, ?>> {
   @Override public MapRow deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return ctxt.readValue(p, MapRow.class);
   }
}