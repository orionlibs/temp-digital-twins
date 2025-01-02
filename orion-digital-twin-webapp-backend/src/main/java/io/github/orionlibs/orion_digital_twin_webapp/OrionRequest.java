package io.github.orionlibs.orion_digital_twin_webapp;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class OrionRequest implements Serializable
{
    private String internalUse;
}