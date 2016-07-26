package com.flatworks.ironrust.client.gui;

import java.util.List;
import java.util.Set;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

/**
 * @author sjx233
 */
public class IronRustModGuiFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraftInstance) {
    }
    
    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return IronRustModConfigGui.class;
    }
    
    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
    
    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }
    
    public static class IronRustModConfigGui extends GuiConfig {
        public IronRustModConfigGui(GuiScreen parentScreen) {
            super(parentScreen, getConfigElements(), IronRustMod.MODID,
                    Configuration.CATEGORY_GENERAL, false, false,
                    GuiConfig.getAbridgedConfigPath(IronRustMod.config.toString()));
        }
        
        private static List<IConfigElement> getConfigElements() {
            return new ConfigElement(IronRustMod.config.getCategory(Configuration.CATEGORY_GENERAL))
                    .getChildElements();
        }
    }
}
