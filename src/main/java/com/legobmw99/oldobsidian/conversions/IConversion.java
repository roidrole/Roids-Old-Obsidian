package com.legobmw99.oldobsidian.conversions;

import com.legobmw99.oldobsidian.matchers.IBlockStateMatcher;
import net.minecraftforge.event.world.BlockEvent;

public interface IConversion {

	//Used for hashing. Set to TrueMatcher if not applicable to your case
	//Do keep in mind that you will then be listening to BlockEvent.NeighborNotifyEvent, with everything that implies for performance.
	IBlockStateMatcher getLiquid1();

	//If extending the class, you don't need to match liquid1, as that one is compared in ConversionSer already with hashing.
	//To circumvent this, use a matcher that is always true and check however you want here.
	//Be wary, however, we are listening to every block update, so exit-early whenever possible
	boolean checkAndPerformConversion(BlockEvent.NeighborNotifyEvent event);

	boolean validate();
}
