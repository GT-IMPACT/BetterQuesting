package betterquesting.api.questing.rewards;

import betterquesting.api.questing.IQuest;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.INBTSaveLoad;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public interface IReward extends INBTSaveLoad<NBTTagCompound>
{
	String getUnlocalisedName();
	ResourceLocation getFactoryID();
	
	boolean canClaim(EntityPlayer player, IQuest quest);
	void claimReward(EntityPlayer player, IQuest quest);
	
	@SideOnly(Side.CLIENT)
    IGuiPanel getRewardGui(IGuiRect rect, IQuest quest);
	
	@Nullable
	@SideOnly(Side.CLIENT)
	GuiScreen getRewardEditor(GuiScreen parent, IQuest quest);
}
