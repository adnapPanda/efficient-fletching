package com.EfficientFletching;

import com.google.common.collect.ImmutableSet;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import static net.runelite.api.AnimationID.*;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.StatChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import java.util.Set;

@Slf4j
@PluginDescriptor(
	name = "efficient-fletching"
)
public class EfficientFletchingPlugin extends Plugin
{
	int setsLeft, imageToUse;
	float shortTimer; //A short timer that will remove the overlay after 30 seconds
	boolean correctOptionSelected;

	EfficientFletchingOverlay counter;

	private static final Set<Integer> FLETCHING_ANIMATION_IDS = ImmutableSet.of(FLETCHING_ATTACH_BOLT_TIPS_TO_ADAMANT_BOLT, FLETCHING_ATTACH_BOLT_TIPS_TO_BLURITE_BOLT,
			FLETCHING_ATTACH_BOLT_TIPS_TO_BRONZE_BOLT, FLETCHING_ATTACH_BOLT_TIPS_TO_DRAGON_BOLT, FLETCHING_ATTACH_BOLT_TIPS_TO_IRON_BROAD_BOLT, FLETCHING_ATTACH_BOLT_TIPS_TO_MITHRIL_BOLT,
			FLETCHING_ATTACH_BOLT_TIPS_TO_RUNE_BOLT, FLETCHING_ATTACH_BOLT_TIPS_TO_STEEL_BOLT, FLETCHING_ATTACH_FEATHERS_TO_ARROWSHAFT, FLETCHING_ATTACH_HEADS);

	//Arrows
	String[] headlessArrows = {"Feather","Arrow shaft"}, bronzeArrows = {"Headless arrow","Bronze arrowtips"}, ironArrows = {"Headless arrow","Iron arrowtips"},
			steelArrows = {"Headless arrow","Steel arrowtips"}, mithrilArrows = {"Headless arrow","Mithril arrowtips"}, broadArrows = {"Headless arrow","Broad arrowheads"},
			adamantArrows = {"Headless arrow","Adamant arrowtips"}, runeArrows = {"Headless arrow","Rune arrowtips"}, amethystArrows = {"Headless arrow","Amethyst arrowtips"},
			dragonArrows = {"Headless arrow","Dragon arrowtips"};

	//Diamond Bolts
	String[] opalDragonBolts = {"Opal bolt tips", "Dragon bolts"}, jadeDragonBolts = {"Jade bolt tips", "Dragon bolts"}, pearlDragonBolts = {"Pearl bolt tips", "Dragon bolts"},
			topazDragonBolts = {"Topaz bolt tips", "Dragon bolts"}, sapphireDragonBolts = {"Sapphire bolt tips", "Dragon bolts"}, emeraldDragonBolts = {"Emerald bolt tips", "Dragon bolts"},
			rubyDragonBolts = {"Ruby bolt tips", "Dragon bolts"}, diamondDragonBolts = {"Diamond bolt tips", "Dragon bolts"}, dragonstoneDragonBolts = {"Dragonstone bolt tips", "Dragon bolts"},
			onyxDragonBolts = {"Onyx bolt tips", "Dragon bolts"};

	//Bolts
	String [] opalBolts = {"Opal bolt tips", "Bronze bolts"}, jadeBolts = {"Jade bolt tips", "Blurite bolts"}, pearlBolts = {"Pearl bolt tips", "Iron bolts"},
			topazBolts = {"Topaz bolt tips", "Steel bolts"}, barbBolts = {"Barb bolttips", "Bronze bolts"}, sapphireBolts = {"Sapphire bolt tips", "Mithril bolts"},
			emeraldBolts = {"Emerald bolt tips", "Mithril bolts"}, rubyBolts = {"Ruby bolt tips", "Adamant bolts"}, diamondBolts = {"Diamond bolt tips", "Adamant bolts"},
			dragonstoneBolts = {"Dragonstone bolt tips", "Runite bolts"}, onyxBolts = {"Onyx bolt tips", "Runite bolts"}, amethystBolts = {"Amethyst bolt tips", "Broad bolts"};

	//Javelins
	String [] bronzeJavelin = {"Bronze javelin heads", "Javelin shaft"}, ironJavelin = {"Iron javelin heads", "Javelin shaft"}, steelJavelin = {"Steel javelin heads", "Javelin shaft"},
			mithrilJavelin = {"Mithril javelin heads", "Javelin shaft"}, adamantJavelin = {"Adamant javelin heads", "Javelin shaft"}, runeJavelin = {"Rune javelin heads", "Javelin shaft"},
			amethystJavelin = {"Amethyst javelin heads", "Javelin shaft"}, dragonJavelin = {"Dragon javelin heads", "Javelin shaft"};

	//Chose Emerald Bolts as picture cause I wanted to
	int HEADLESS_ARROWS = 53, DRAGON_BOLTS = 21905, JAVELIN_SHAFT = 19584, EMERALD_BOLTS = 9338;


	@Inject
	private Client client;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	@Override
	protected void startUp() throws Exception
	{
		setsLeft = 0;
		shortTimer = 30;
		correctOptionSelected = false;
		counter = null;
	}

	@Override
	protected void shutDown() throws Exception
	{
		closeCounter();
	}

	private void updateCounter() {
		if (counter == null)
		{
			counter = new EfficientFletchingOverlay(itemManager.getImage(imageToUse), this, setsLeft);
			infoBoxManager.addInfoBox(counter);
		}
		else
		{
			counter.addSets(setsLeft);
			counter.setImage(itemManager.getImage(imageToUse)); // Update overlay image
			infoBoxManager.updateInfoBoxImage(counter);
		}
	}

	@Subscribe
	private void onGameTick(GameTick event) {
		shortTimer -= 0.6;
		if (shortTimer <= 0) {
			closeCounter();
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged event) {
		if (event.getSkill() == Skill.FLETCHING && correctOptionSelected) {
			shortTimer = 30;
			setsLeft -= 1;
			updateCounter();
			if (setsLeft == 0) {
				correctOptionSelected = false;
			}
		} else {
			correctOptionSelected = false;
		}
	}

	public static boolean containsWords(String inputString, String[] items) {
		boolean found = true;
		for (String item : items) {
			if (!inputString.contains(item)) {
				found = false;
				break;
			}
		}
		return found;
	}

	private void closeCounter() {
		if (counter != null) {
			infoBoxManager.removeInfoBox(counter);
			counter = null;
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked) {
		String target = menuOptionClicked.getMenuTarget();

		//Arrows
		if (containsWords(target, headlessArrows) || containsWords(target, bronzeArrows) || containsWords(target, ironArrows) || containsWords(target, steelArrows)
				|| containsWords(target, mithrilArrows) || containsWords(target, broadArrows) || containsWords(target, adamantArrows) || containsWords(target, runeArrows)
				|| containsWords(target, amethystArrows) || containsWords(target, dragonArrows)) {
			imageToUse = HEADLESS_ARROWS;
			correctOptionSelected = true;
			setsLeft = 10;
		}

		//Diamond Bolts
		else if (containsWords(target, opalDragonBolts) || containsWords(target, jadeDragonBolts) || containsWords(target, pearlDragonBolts) || containsWords(target, topazDragonBolts)
				|| containsWords(target, sapphireDragonBolts) || containsWords(target, emeraldDragonBolts) || containsWords(target, rubyDragonBolts) || containsWords(target, diamondDragonBolts)
				|| containsWords(target, dragonstoneDragonBolts) || containsWords(target, onyxDragonBolts)) {
			imageToUse = DRAGON_BOLTS;
			correctOptionSelected = true;
			setsLeft = 10;
		}

		//Bolts
		else if (containsWords(target, opalBolts) || containsWords(target, jadeBolts) || containsWords(target, pearlBolts) || containsWords(target, topazBolts)
				|| containsWords(target, barbBolts) || containsWords(target, sapphireBolts) || containsWords(target, emeraldBolts) || containsWords(target, rubyBolts)
				|| containsWords(target, diamondBolts) || containsWords(target, dragonstoneBolts) || containsWords(target, onyxBolts) || containsWords(target, amethystBolts)) {
			imageToUse = EMERALD_BOLTS;
			correctOptionSelected = true;
			setsLeft = 10;
		}

		//Javelins
		else if (containsWords(target, bronzeJavelin) || containsWords(target, ironJavelin) || containsWords(target, steelJavelin) || containsWords(target, mithrilJavelin)
				|| containsWords(target, adamantJavelin) || containsWords(target, runeJavelin) || containsWords(target, amethystJavelin) || containsWords(target, dragonJavelin)) {
			imageToUse = JAVELIN_SHAFT;
			correctOptionSelected = true;
			setsLeft = 10;
		} else if (String.valueOf(menuOptionClicked.getMenuAction()) == "ITEM_USE_ON_WIDGET_ITEM") { //If any other fletching action/action is selected then set to false
			correctOptionSelected = false;
		}
	}
}
