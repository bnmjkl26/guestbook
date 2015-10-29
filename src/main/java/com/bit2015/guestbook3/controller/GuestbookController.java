package com.bit2015.guestbook3.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bit2015.guestbook3.dao.GuestbookDao;
import com.bit2015.guestbook3.vo.GuestbookVo;

@Controller
public class GuestbookController {
	
	@Autowired
	GuestbookDao dao;

	@RequestMapping( "/" )
	public String index( Model model ) {
		List<GuestbookVo> list = dao.getList();
		model.addAttribute( "list", list );
		return "/WEB-INF/views/index.jsp";
	}

	@RequestMapping( "/insert" )
	public String insert( @ModelAttribute GuestbookVo vo ) {
		dao.insert(vo);
		return "redirect:/";
	}
	
	@RequestMapping( "/deleteform/{no}" )
	public String deleteform( @PathVariable( "no" ) Long no, Model model ) {
		model.addAttribute( "no", no );
		return "/WEB-INF/views/deleteform.jsp";
	}
	
	@RequestMapping( "/delete" )
	public String delete( @ModelAttribute GuestbookVo vo ) {
		dao.delete(vo);
		return "redirect:/";
	}	
}