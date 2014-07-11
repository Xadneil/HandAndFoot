package com.xadneil.client.tests;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.xadneil.client.Card;
import com.xadneil.client.Group;
import com.xadneil.client.Suit;
import com.xadneil.client.net.Packet;
import com.xadneil.server.net.SendOpcode;

public class PacketTest {

	@Test
	public void test() {
		Packet p;
		p = new Packet(SendOpcode.BOARD);
		assertEquals("opcode constructor", SendOpcode.BOARD.getValue(),
				p.getOpcode());

		p = new Packet(new byte[] { 0, 1, 1, 0, 0, 0 });
		assertEquals("array constructor opcode", 0, p.getOpcode());
		assertEquals("array constructor byte", 1, p.getByte());
		assertEquals("array constructor int", 1, p.getInt());

		p = new Packet(new byte[] { 0 });
		p.addInt(-42);
		p.addInt(42);
		p.addInt(-400);
		p.addInt(400);
		p.addInt(-400000);
		p.addInt(400000);
		p.addInt(Integer.MIN_VALUE);
		p.addInt(Integer.MAX_VALUE);
		assertEquals("addInt negative", -42, p.getInt());
		assertEquals("addInt positive", 42, p.getInt());
		assertEquals("addInt big negative", -400, p.getInt());
		assertEquals("addInt big positive", 400, p.getInt());
		assertEquals("addInt bigger negative", -400000, p.getInt());
		assertEquals("addInt bigger positive", 400000, p.getInt());
		assertEquals("addInt min", Integer.MIN_VALUE, p.getInt());
		assertEquals("addInt max", Integer.MAX_VALUE, p.getInt());
		
		p = new Packet(new byte[] { 0 });
		Card c = new Card(1, Suit.HEART);
		p.addCard(c);
		assertEquals("addCard, getCard", c, p.getCard());
		
		p = new Packet(new byte[] { 0 });
		Group g = new Group(1, 0);
		g.addCards(Arrays.asList(c, c));
		p.addGroup(g);
		assertEquals("addGroup, getGroup", g, p.getGroup());
		
		p = new Packet(new byte[] { 0 });
		String s = "test String 10 ~!)%#$";
		p.add(s);
		assertEquals("string", s, p.getString());
	}

}
