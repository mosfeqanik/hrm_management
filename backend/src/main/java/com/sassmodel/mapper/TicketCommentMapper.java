package com.sassmodel.mapper;

import com.sassmodel.dto.TicketCommentResponse;
import com.sassmodel.entity.TicketComment;

public final class TicketCommentMapper {

    private TicketCommentMapper() {
    }

    public static TicketCommentResponse toResponse(TicketComment comment) {
        return new TicketCommentResponse(
                comment.getId(),
                comment.getTicket().getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getFullName(),
                comment.getBody(),
                comment.isInternal()
        );
    }
}
