package dev.penguinz.earlyinternet;

import dev.penguinz.Sylk.Application;
import dev.penguinz.Sylk.assets.options.FontOptions;
import dev.penguinz.Sylk.assets.options.TextureOptions;
import dev.penguinz.Sylk.graphics.texture.Texture;
import dev.penguinz.Sylk.ui.font.Font;
import dev.penguinz.Sylk.util.Disposable;

public class Assets implements Disposable {

    private static Assets assets = new Assets();

    private Font font;

    private Texture playerTexture;
    private Texture computerTexture;

    public void loadAssets() {
        Application.getInstance().getAssets().loadAsset("font.ttf", Font.class, new FontOptions()
                .setAssetLoadedCallback(font -> this.font = font));
        Application.getInstance().getAssets().loadAsset("bug.png", Texture.class, new TextureOptions()
                .setAssetLoadedCallback(texture -> this.playerTexture = texture));
        Application.getInstance().getAssets().loadAsset("compicon.png", Texture.class, new TextureOptions()
                .setAssetLoadedCallback(texture -> this.computerTexture = texture));



        Application.getInstance().getAssets().finishLoading();
    }

    public Font getFont() {
        return font;
    }

    public Texture getPlayerTexture() {
        return playerTexture;
    }

    public Texture getComputerTexture() {
        return computerTexture;
    }

    @Override
    public void dispose() {
        font.dispose();
    }

    public static Assets getAssets() {
        return assets;
    }
}
