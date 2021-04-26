module diskord.payload {
  exports diskord.payload;

  requires lombok;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;

  opens diskord.payload to com.fasterxml.jackson.databind;
}