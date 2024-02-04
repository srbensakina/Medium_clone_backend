package com.api.medium_clone.service;

import com.api.medium_clone.entity.Tag;
import com.api.medium_clone.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService{

   private final TagRepository tagRepository;
    @Override
    public List<String> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }

}
