package dev.penguinz.earlyinternet;

import dev.penguinz.Sylk.Application;
import dev.penguinz.Sylk.Camera;
import dev.penguinz.Sylk.OrthographicCamera;
import dev.penguinz.Sylk.animation.Animation;
import dev.penguinz.Sylk.animation.Animator;
import dev.penguinz.Sylk.event.Event;
import dev.penguinz.Sylk.graphics.MainRenderer;
import dev.penguinz.Sylk.graphics.VAO;
import dev.penguinz.Sylk.graphics.lighting.AmbientLight;
import dev.penguinz.Sylk.input.Key;
import dev.penguinz.Sylk.ui.UIContainer;
import dev.penguinz.Sylk.ui.UIText;
import dev.penguinz.Sylk.ui.constraints.AbsoluteConstraint;
import dev.penguinz.Sylk.ui.constraints.PixelConstraint;
import dev.penguinz.Sylk.ui.constraints.RelativeConstraint;
import dev.penguinz.Sylk.ui.constraints.UIConstraints;
import dev.penguinz.Sylk.ui.font.RelativeTextHeight;
import dev.penguinz.Sylk.util.Alignment;
import dev.penguinz.Sylk.util.Color;
import dev.penguinz.Sylk.util.Layer;
import dev.penguinz.Sylk.util.maths.Transform;
import dev.penguinz.earlyinternet.menu.HamButton;
import dev.penguinz.earlyinternet.menu.SidePanel;
import dev.penguinz.earlyinternet.world.World;

public class Game implements Layer {

    private MainRenderer renderer;
    private OrthographicCamera camera;

    private AmbientLight ambience;

    private World world;
    private Player player;

    private Animator animator;
    private UIContainer container;
    private UIText infectedText;
    private UIText tpsText;
    private SidePanel sidePanel;

    private boolean toggled = false;

    @Override
    public void init() {
        this.renderer = new MainRenderer();
        this.camera = new OrthographicCamera(5);
        this.ambience = new AmbientLight(Color.white, 1);

        this.world = new World(this);
        this.player = new Player(this.world.getSpawnPoint());

        this.animator = new Animator();
        this.container = new UIContainer();
        this.infectedText = new UIText("", Assets.getAssets().getFont(), new RelativeTextHeight(1))
                .setHorizontalAlignment(Alignment.RIGHT)
                .setVerticalAlignment(Alignment.BOTTOM);
        this.container.addComponent(this.infectedText,
                new UIConstraints().
                setXConstraint(new PixelConstraint(5, Alignment.RIGHT)).setYConstraint(new PixelConstraint(-5, Alignment.BOTTOM)).
                setWidthConstraint(new RelativeConstraint(1)).setHeightConstraint(new AbsoluteConstraint(25)));

        this.tpsText = new UIText("", Assets.getAssets().getFont(), new RelativeTextHeight(1))
                .setHorizontalAlignment(Alignment.RIGHT)
                .setVerticalAlignment(Alignment.BOTTOM);
        this.container.addComponent(this.tpsText,
                new UIConstraints().
                        setXConstraint(new PixelConstraint(5, Alignment.RIGHT)).setYConstraint(new PixelConstraint(20, Alignment.BOTTOM)).
                        setWidthConstraint(new RelativeConstraint(1)).setHeightConstraint(new AbsoluteConstraint(25)));

        sidePanel = new SidePanel(this);
        UIConstraints sidePanelConstraints = new UIConstraints().
                setXConstraint(new PixelConstraint(-275, Alignment.RIGHT)).setYConstraint(new PixelConstraint(0, Alignment.TOP)).
                setWidthConstraint(new AbsoluteConstraint(275)).setHeightConstraint(new RelativeConstraint(0.75f));
        this.container.addComponent(sidePanel, sidePanelConstraints);

        Animation slideAnimation = new Animation(0.25f).addValue(this.sidePanel.getConstraints().getXAnimatableConstraint(), -275, 0);

        this.container.addComponent(new HamButton(() -> {
            toggled = !toggled;
            if(toggled)
                animator.playAnimation(slideAnimation);
            else
                animator.playAnimation(slideAnimation, true);
        }), new UIConstraints().
                setXConstraint(new PixelConstraint(0, Alignment.RIGHT)).setYConstraint(new PixelConstraint(0, Alignment.TOP)).
                setWidthConstraint(new AbsoluteConstraint(50)).setHeightConstraint(new AbsoluteConstraint(50)));
    }

    @Override
    public void update() {
        if(Application.getInstance().getInput().isKeyDown(Key.KEY_RIGHT_BRACKET)) {
            this.camera.zoom += 0.1f;
            this.camera = new OrthographicCamera(this.camera.zoom);
        }
        if(Application.getInstance().getInput().isKeyDown(Key.KEY_LEFT_BRACKET)) {
            this.camera.zoom -= 0.1f;
            this.camera.zoom = Math.max(this.camera.zoom, 0.1f);
            this.camera = new OrthographicCamera(this.camera.zoom);
        }
        this.player.update(camera);
        this.camera.update();

        this.world.update();

        this.container.update();
        this.infectedText.setText(((int) (world.getInfectionPercent() * 100))+" percent infected");

        if(Application.getInstance().getInput().isKeyDown(Key.KEY_EQUAL)) this.world.setTicks(this.world.getTicks()+1);
        if(Application.getInstance().getInput().isKeyDown(Key.KEY_MINUS)) this.world.setTicks(this.world.getTicks()-1);
        this.tpsText.setText(world.getTicks()+" tps");
        this.sidePanel.update();
        this.animator.update();
    }

    @Override
    public void render() {
        this.renderer.begin(camera);
        this.renderer.addLight(ambience);
        this.world.render(this.renderer, this.camera);
        this.renderer.finish();
        this.world.renderCommunications(this.camera);
        this.player.render(this.camera, ambience);

        this.container.render();
    }

    @Override
    public void onEvent(Event event) {
        this.camera.onEvent(event);
        this.container.onEvent(event);
    }

    @Override
    public void dispose() {
        this.renderer.dispose();
        this.container.dispose();
        this.player.dispose();
    }

    public Player getPlayer() {
        return player;
    }

    public World getWorld() {
        return world;
    }
}
