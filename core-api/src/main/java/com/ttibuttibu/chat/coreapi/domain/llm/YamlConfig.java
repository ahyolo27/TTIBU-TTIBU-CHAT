package com.ttibuttibu.chat.coreapi.domain.llm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YamlConfig(
        /* YAML 키와 동일 */
        List<YamlModel> model_list
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record YamlModel(String model_name, LiteParams litellm_params) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LiteParams(String model) {}

}
