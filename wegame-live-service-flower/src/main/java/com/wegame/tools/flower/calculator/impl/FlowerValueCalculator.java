package com.wegame.tools.flower.calculator.impl;


import com.wegame.tools.flower.Player;
import com.wegame.tools.flower.PlayerUtil;
import com.wegame.tools.flower.calculator.PlayerType;
import com.wegame.tools.flower.calculator.ValueCalculator;

/**
 * 花色参与牌值大小比较的计算器，牌值越大，牌越大
 *
 * @author Leon
 *
 */
public class FlowerValueCalculator implements ValueCalculator {

	private int getFlowerValue(Player player) {
		return player.cards[0].getFlower() * 16 + player.cards[1].getFlower() * 4 + player.cards[2].getFlower();
	}

	private int getA32FlowerValue(Player player){
		return player.cards[1].getFlower() * 16 + player.cards[2].getFlower() * 4 + player.cards[0].getFlower();
	}

	// 获取炸弹牌值绝对大小
	public int getBombValue(Player player) {
		// 炸弹需要先对牌按花色大小排序，保证比如黑桃A红桃A方片A会>红桃A梅花A方片A
		PlayerUtil.sortPlayerByFlower(player);
		return (player.cards[0].getNumber() + PlayerType.STRAIGHT_FLUSH_MAX_VALUE) * 64 + getFlowerValue(player);
	}

	// 获取同花顺牌值绝对大小,A32也是同花顺，是最小的同花顺(参考自百度百科)
	public int getStraightFlushValue(Player player) {
		if (player.isA32()) {
			return (1 + PlayerType.FLUSH_MAX_VALUE) * 64 + getA32FlowerValue(player);
		}
		return (player.cards[2].getNumber() + PlayerType.FLUSH_MAX_VALUE) * 64 + getFlowerValue(player);
	}

	// 获取同花牌值绝对大小
	public int getFlushValue(Player player) {
		return (player.cards[0].getNumber() * 256 + player.cards[1].getNumber() * 16 + player.cards[2].getNumber()
				+ PlayerType.STRAIGHT_MAX_VALUE) * 64 + getFlowerValue(player);
	}

	// 获取顺子牌值绝对大小
	public int getStraightValue(Player player) {
		if (player.isA32()) {
			return (1 + PlayerType.DOUBLE_MAX_VALUE) * 64 + getA32FlowerValue(player);
		}
		return (player.cards[2].getNumber() + PlayerType.DOUBLE_MAX_VALUE) * 64 + getFlowerValue(player);
	}

	// 获取对子牌值绝对大小
	// 在判断牌型时，如果是对子，则将对子放在数组前面两位
	public int getDoubleValue(Player player) {
		// 在花色参与计算大小时，将对子中的花色大的换到前面
		PlayerUtil.exchangeSortedDoubleFlower(player);
		return (player.cards[1].getNumber() * 16 + player.cards[2].getNumber() + PlayerType.NORMAL_MAX_VALUE) * 64
				+ getFlowerValue(player);
	}

	// 获取普通牌值绝对大小
	public int getNormalValue(Player player) {
		return (player.cards[0].getNumber() * 256 + player.cards[1].getNumber() * 16 + player.cards[2].getNumber()) * 64
				+ getFlowerValue(player);
	}
}
