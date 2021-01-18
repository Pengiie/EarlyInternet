package dev.penguinz.earlyinternet.menu;

import dev.penguinz.Sylk.ui.UIBlock;
import dev.penguinz.Sylk.ui.UIButton;
import dev.penguinz.Sylk.ui.UIEventListener;
import dev.penguinz.Sylk.ui.UIText;
import dev.penguinz.Sylk.ui.constraints.AbsoluteConstraint;
import dev.penguinz.Sylk.ui.constraints.PixelConstraint;
import dev.penguinz.Sylk.ui.constraints.RelativeConstraint;
import dev.penguinz.Sylk.ui.constraints.UIConstraints;
import dev.penguinz.Sylk.ui.font.PixelTextHeight;
import dev.penguinz.Sylk.ui.font.RelativeTextHeight;
import dev.penguinz.Sylk.util.Alignment;
import dev.penguinz.Sylk.util.Color;
import dev.penguinz.earlyinternet.Assets;
import dev.penguinz.earlyinternet.Game;
import dev.penguinz.earlyinternet.world.World;

public class SidePanel extends UIBlock {

    private final Game game;

    private final UIText pointsText;
    private final BuyButton antiButton, lingeringButton, transmissionButton;

    public SidePanel(Game game) {
        super(new Color(0.2f, 0.2f, 0.2f, 1f), 2);
        this.game = game;

        this.pointsText = new UIText("0 points", Color.white, Assets.getAssets().getFont(), new RelativeTextHeight(1)).
                setHorizontalAlignment(Alignment.LEFT).
                setVerticalAlignment(Alignment.CENTER);
        this.addComponent(this.pointsText, new UIConstraints().
                setXConstraint(new PixelConstraint(15, Alignment.LEFT)).setYConstraint(new PixelConstraint(15, Alignment.TOP)).
                setWidthConstraint(new AbsoluteConstraint(225f)).setHeightConstraint(new AbsoluteConstraint(30)));

        this.antiButton = new AntiButton(game.getWorld());
        this.addComponent(this.antiButton, new UIConstraints().
                setXConstraint(new RelativeConstraint(0.05f)).setYConstraint(new PixelConstraint(100, Alignment.TOP)).
                setWidthConstraint(new RelativeConstraint(0.9f)).setHeightConstraint(new AbsoluteConstraint(100)));

        this.lingeringButton = new LingeringButton(game.getWorld());
        this.addComponent(this.lingeringButton, new UIConstraints().
                setXConstraint(new RelativeConstraint(0.05f)).setYConstraint(new PixelConstraint(230, Alignment.TOP)).
                setWidthConstraint(new RelativeConstraint(0.9f)).setHeightConstraint(new AbsoluteConstraint(100)));

        this.transmissionButton = new TransmissionButton(game.getWorld());
        this.addComponent(this.transmissionButton, new UIConstraints().
                setXConstraint(new RelativeConstraint(0.05f)).setYConstraint(new PixelConstraint(360, Alignment.TOP)).
                setWidthConstraint(new RelativeConstraint(0.9f)).setHeightConstraint(new AbsoluteConstraint(100)));
    }

    public void update() {
        this.pointsText.setText(game.getWorld().totalInfected+" points");
        antiButton.update();
        lingeringButton.update();
        transmissionButton.update();
    }

    private static class BuyButton extends UIBlock implements UIEventListener {

        protected int stage = 0;
        protected final int max;
        protected int cost = 0;
        protected final World world;

        private final UIText counter;
        private final UIText costLabel;

        public BuyButton(World world, String title, String description, int max) {
            super(new Color(25, 27, 31, 0.1f), 2.5f);
            this.world = world;
            this.max = max;

            UIBlock base = new UIBlock(new Color(0.17f, 0.17f, 0.17f, 0.1f), 2.5f);
            this.addComponent(base, new UIConstraints().
                    setXConstraint(new RelativeConstraint(0.025f)).setYConstraint(new RelativeConstraint(0.05f)).
                    setWidthConstraint(new RelativeConstraint(0.95f)).setHeightConstraint(new RelativeConstraint(0.9f))
            );

            this.addComponent(new UIText(title, Color.white, Assets.getAssets().getFont(), new PixelTextHeight(20)).setHorizontalAlignment(Alignment.LEFT).setVerticalAlignment(Alignment.TOP),
                    new UIConstraints().
                            setXConstraint(new PixelConstraint(12)).setYConstraint(new PixelConstraint(5, Alignment.TOP)).
                            setWidthConstraint(new RelativeConstraint(0.98f)).setHeightConstraint(new RelativeConstraint(0.5f)));

            this.addComponent(new UIText(description, Color.white, Assets.getAssets().getFont(), new PixelTextHeight(14)).setHorizontalAlignment(Alignment.LEFT),
                    new UIConstraints().
                    setXConstraint(new PixelConstraint(12)).setYConstraint(new PixelConstraint(20, Alignment.TOP)).
                    setWidthConstraint(new RelativeConstraint(0.95f)).setHeightConstraint(new RelativeConstraint(0.5f)));

            this.addComponent(this.counter = new UIText("", Color.white, Assets.getAssets().getFont(), new PixelTextHeight(18)).setHorizontalAlignment(Alignment.LEFT).setVerticalAlignment(Alignment.BOTTOM),
                    new UIConstraints().
                            setXConstraint(new PixelConstraint(12)).setYConstraint(new PixelConstraint(65, Alignment.TOP)).
                            setWidthConstraint(new RelativeConstraint(0.95f)).setHeightConstraint(new RelativeConstraint(0.2f)));

            this.addComponent(this.costLabel = new UIText("", Color.white, Assets.getAssets().getFont(), new PixelTextHeight(14)).setHorizontalAlignment(Alignment.LEFT).setVerticalAlignment(Alignment.BOTTOM),
                    new UIConstraints().
                            setXConstraint(new PixelConstraint(12)).setYConstraint(new PixelConstraint(75, Alignment.TOP)).
                            setWidthConstraint(new RelativeConstraint(0.95f)).setHeightConstraint(new RelativeConstraint(0.2f)));
        }

        public void update() {
            this.counter.setText(stage+" out of "+max);
            this.costLabel.setText(stage == max ? "Maxed" : "Cost "+cost+" points");
        }

    }

    private static class AntiButton extends BuyButton {

        // 1 - 100 point    20%
        // 2 - 500 points   40%
        // 3 - 1000 points  60%
        // 4 - 1500 points  80%
        // 5 - 2000 points 100%
        public AntiButton(World world) {
            super(world, "Signature Scrambling", "Negates the antivirus protection on past infected computers", 5);
            this.cost = 100;
        }

        @Override
        public void onMouseClicked(int button) {
            if(button == 0 && stage < max) {
                if(world.totalInfected >= cost) {
                    world.totalInfected -= cost;
                    stage++;

                    if(stage == 1) {
                        world.antivirusPreventionModifier = 0.8f;
                        cost = 500;
                    } else if(stage == 2) {
                        world.antivirusPreventionModifier = 0.6f;
                        cost = 750;
                    } else if(stage == 3) {
                        world.antivirusPreventionModifier = 0.4f;
                        cost = 1000;
                    } else if(stage == 4) {
                        world.antivirusPreventionModifier = 0.2f;
                        cost = 1000;
                    } else if(stage == 5) {
                        world.antivirusPreventionModifier = 0f;
                        cost = -20;
                    }
                }
            }
        }
    }

    private static class LingeringButton extends BuyButton {

        // 1 - 10 point    5%
        // 2 - 40 points   10%
        // 3 - 100 points  15%
        // 4 - 500 points  20%
        // 5 - 1000 points  30%
        // 6 - 2000 points  40%
        // 7 - 4000 points  55%
        // 8 - 5000 points  75%
        // 9 - 7500 points  90%
        // 10 - 10000 points  150%
        public LingeringButton(World world) {
            super(world, "Detectability", "Makes the virus last longer on computers", 10);
            this.cost = 10;
        }

        @Override
        public void onMouseClicked(int button) {
            if(button == 0 && stage < max) {
                if(world.totalInfected >= cost) {
                    world.totalInfected -= cost;
                    stage++;

                    if(stage == 1) {
                        world.lingeringModifier = 1.05f;
                        cost = 40;
                    } else if(stage == 2) {
                        world.lingeringModifier = 1.1f;
                        cost = 100;
                    } else if(stage == 3) {
                        world.lingeringModifier = 1.15f;
                        cost = 500;
                    } else if(stage == 4) {
                        world.lingeringModifier = 1.2f;
                        cost = 1000;
                    } else if(stage == 5) {
                        world.lingeringModifier = 1.3f;
                        cost = 2000;
                    } else if(stage == 6) {
                        world.lingeringModifier = 1.4f;
                        cost = 4000;
                    } else if(stage == 7) {
                        world.lingeringModifier = 1.55f;
                        cost = 5000;
                    } else if(stage == 8) {
                        world.lingeringModifier = 1.75f;
                        cost = 7500;
                    } else if(stage == 9) {
                        world.lingeringModifier = 1.9f;
                        cost = 10000;
                    } else if(stage == 10) {
                        world.lingeringModifier = 2.5f;
                        cost = -20;
                    }
                }
            }
        }
    }

    private static class TransmissionButton extends BuyButton {

        // 1 - 5 point    5%
        // 2 - 10 points  10%
        // 3 - 20 points  15%
        // 4 - 50 points  20%
        // 5 - 100 points  25%
        // 6 - 150 points  30%
        // 7 - 200 points  35%
        // 8 - 400 points  40%
        // 9 - 600 points  45%
        // 10 - 800 points  50%
        // 11 - 1000 points  55%
        // 12 - 2000 points  65%
        // 13 - 3000 points  75%
        // 14 - 4000 points  80%
        // 15 - 5000 points  85%
        // 16 - 10000 points  100%
        public TransmissionButton(World world) {
            super(world, "Transmission", "Infection speed increases when infecting", 16);
            this.cost = 5;
        }

        @Override
        public void onMouseClicked(int button) {
            if(button == 0 && stage < max) {
                if(world.totalInfected >= cost) {
                    world.totalInfected -= cost;
                    stage++;

                    if(stage == 1) {
                        world.spreadModifier = 0.05f;
                        cost = 10;
                    } else if(stage == 2) {
                        world.spreadModifier = 0.1f;
                        cost = 20;
                    } else if(stage == 3) {
                        world.spreadModifier = 0.15f;
                        cost = 50;
                    } else if(stage == 4) {
                        world.spreadModifier = 0.2f;
                        cost = 100;
                    } else if(stage == 5) {
                        world.spreadModifier = 0.25f;
                        cost = 150;
                    } else if(stage == 6) {
                        world.spreadModifier = 0.3f;
                        cost = 200;
                    } else if(stage == 7) {
                        world.spreadModifier = 0.35f;
                        cost = 300;
                    } else if(stage == 8) {
                        world.spreadModifier = 0.45f;
                        cost = 400;
                    } else if(stage == 9) {
                        world.spreadModifier = 0.5f;
                        cost = 500;
                    } else if(stage == 10) {
                        world.spreadModifier = 0.55f;
                        cost = 750;
                    } else if(stage == 11) {
                        world.spreadModifier = 0.6f;
                        cost = 1000;
                    } else if(stage == 12) {
                        world.spreadModifier = 0.65f;
                        cost = 1000;
                    } else if(stage == 13) {
                        world.spreadModifier = 0.7f;
                        cost = 2000;
                    } else if(stage == 14) {
                        world.spreadModifier = 0.75f;
                        cost = 2000;
                    } else if(stage == 15) {
                        world.spreadModifier = 0.8f;
                        cost = 5000;
                    } else if(stage == 16) {
                        world.spreadModifier = 1f;
                        cost = -20;
                    }
                }
            }
        }
    }

}
