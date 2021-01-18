package dev.penguinz.earlyinternet.menu;

import dev.penguinz.Sylk.Application;
import dev.penguinz.Sylk.animation.values.AnimatableColor;
import dev.penguinz.Sylk.graphics.texture.Texture;
import dev.penguinz.Sylk.ui.UIBlock;
import dev.penguinz.Sylk.ui.UIContainer;
import dev.penguinz.Sylk.ui.UIEventListener;
import dev.penguinz.Sylk.ui.UIImage;
import dev.penguinz.Sylk.ui.constraints.CenterConstraint;
import dev.penguinz.Sylk.ui.constraints.RelativeConstraint;
import dev.penguinz.Sylk.ui.constraints.UIConstraints;
import dev.penguinz.Sylk.util.Color;

public class HamButton extends UIBlock implements UIEventListener {

    private Runnable onClick;

    private boolean toggled = false;

    public HamButton(Runnable onClick) {
        super(new Color(0.4f, 0.4f, 0.4f, 0.75f), 2);
        this.onClick = onClick;

        UIConstraints constraints = new UIConstraints().
                setXConstraint(new CenterConstraint()).setWidthConstraint(new RelativeConstraint(0.8f))
                .setYConstraint(new RelativeConstraint(0.175f)).setHeightConstraint(new RelativeConstraint(0.1f));

        addComponent(new UIBlock(new Color(0.9f, 0.9f, 0.9f, 0.95f), 1), constraints);
        addComponent(new UIBlock(new Color(0.9f, 0.9f, 0.9f, 0.95f), 1), new UIConstraints(constraints).setYConstraint(new RelativeConstraint(0.45f)));
        addComponent(new UIBlock(new Color(0.9f, 0.9f, 0.9f, 0.95f), 1), new UIConstraints(constraints).setYConstraint(new RelativeConstraint(0.725f)));
    }

    @Override
    public void onMouseEnter() {
        this.color.value = new Color(0.3f, 0.3f, 0.3f, 0.85f);
    }

    @Override
    public void onMouseExit() {
        this.color.value = new Color(0.4f, 0.4f, 0.4f, 0.75f);
    }

    @Override
    public void onMouseClicked(int button) {
        if(button == 0) {
            toggled = !toggled;
            onClick.run();
        }
    }

    public boolean isToggled() {
        return toggled;
    }
}
