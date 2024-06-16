package com.Social.dto.mapper;

import java.util.ArrayList;
import java.util.List;

import com.Social.dto.ReelsDto;
import com.Social.dto.UserDto;
import com.Social.model.Reels;

public class ReelsDtoMapper {
	
	
	public static ReelsDto toReelsDto(Reels reel) {
		ReelsDto reelsDto=new ReelsDto();
		
		UserDto user=UserDtoMapper.userDTO(reel.getUser());
		
		reelsDto.setTitle(reel.getTitle());
		reelsDto.setUser(user);
		reelsDto.setVideo(reel.getVideo());
		reelsDto.setId(reel.getId());
		
		return reelsDto;
		
	}
	
	public static List<ReelsDto> toReelsDtos(List<Reels> reels){
		
		List<ReelsDto> reelsDtos=new ArrayList<>();
		
		for(Reels reel : reels ) {
			 ReelsDto reelsDto=toReelsDto(reel);
			 reelsDtos.add(reelsDto);
		}
		
		return reelsDtos;
		
	}

}
