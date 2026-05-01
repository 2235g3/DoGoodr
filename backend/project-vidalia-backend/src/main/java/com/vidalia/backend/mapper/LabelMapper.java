package com.vidalia.backend.mapper;

import com.vidalia.backend.dto.label.CreateLabelDTO;
import com.vidalia.backend.dto.label.LabelDTO;
import com.vidalia.backend.model.matchmaking.Label;
import org.springframework.stereotype.Component;

@Component
public class LabelMapper {

    public Label toEntity(CreateLabelDTO dto) {
        Label label = new Label();
        label.setName(dto.getName());
        label.setSemanticTag(dto.getSemanticTag());
        label.setRequired(dto.isRequired());
        label.setType(dto.getType());
        return label;
    }

    public LabelDTO toDTO(Label label) {
        LabelDTO dto = new LabelDTO();
        dto.setId(label.getId());
        dto.setName(label.getName());
        dto.setSemanticTag(label.getSemanticTag() != null ? label.getSemanticTag().getName() : null);
        dto.setRequired(label.isRequired());
        dto.setType(label.getType());
        return dto;
    }

    public void updateEntity(Label label, CreateLabelDTO dto) {
        if (dto.getName() != null) {
            label.setName(dto.getName());
        }
        label.setSemanticTag(dto.getSemanticTag());
        label.setRequired(dto.isRequired());
        if (dto.getType() != null) {
            label.setType(dto.getType());
        }
    }
}
