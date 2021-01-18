package dev.penguinz.earlyinternet;

import dev.penguinz.Sylk.Application;
import dev.penguinz.Sylk.Camera;
import dev.penguinz.Sylk.Time;
import dev.penguinz.Sylk.graphics.MainRenderer;
import dev.penguinz.Sylk.graphics.Material;
import dev.penguinz.Sylk.graphics.RenderLayer;
import dev.penguinz.Sylk.graphics.VAO;
import dev.penguinz.Sylk.graphics.lighting.Light;
import dev.penguinz.Sylk.graphics.post.effects.BloomEffect;
import dev.penguinz.Sylk.particles.Particle;
import dev.penguinz.Sylk.particles.ParticleBuilder;
import dev.penguinz.Sylk.particles.ParticleEmitter;
import dev.penguinz.Sylk.particles.ParticleRenderer;
import dev.penguinz.Sylk.tasks.TaskScheduler;
import dev.penguinz.Sylk.util.Color;
import dev.penguinz.Sylk.util.Disposable;
import dev.penguinz.Sylk.util.RefContainer;
import dev.penguinz.Sylk.util.maths.Transform;
import dev.penguinz.Sylk.util.maths.Vector2;

public class Player implements Disposable {

    private Transform transform;

    private Material material;

    private final float speed = 10;
    private final float emissionFrequency = 0.5f;
    private final float emissionSpeed = 0.4f;

    private TaskScheduler scheduler;
    private ParticleEmitter emitter;
    private ParticleRenderer particleRenderer;

    private MainRenderer playerRenderer;

    private Vector2 velocity = new Vector2();

    private final Particle EMISSION_PARTICLE =
            new ParticleBuilder().
                    setStartColor(new Color(66, 17, 5, 0.2f)).
                    setEndColor(new Color(36, 7, 2, 0.7f)).
                    setStartSize(new Vector2(0.1f, 0.1f)).
                    setEndSize(new Vector2(0.05f, 0.05f)).
                    build();

    public Player(Vector2 spawn) {
        this.transform = new Transform(new Vector2(spawn), new Vector2(0.25f, 0.25f));
        this.material = new Material(new RefContainer<>(Assets.getAssets().getPlayerTexture()));

        this.playerRenderer = new MainRenderer(RenderLayer.RENDER2);

        this.particleRenderer = new ParticleRenderer(RenderLayer.RENDER1);
        this.emitter = new ParticleEmitter(new Vector2(this.transform.position));
        this.scheduler = new TaskScheduler();
        this.scheduler.scheduleRepeatingTask(() -> {
            for (int i = 0; i < 6; i++) {
                Vector2 velocity = new Vector2((float) Math.random() * 2 - 1, (float) Math.random() * 2 - 1);
                velocity.normalize();
                velocity.mul(emissionSpeed);
                velocity.add(this.velocity);
                emitter.emit(EMISSION_PARTICLE, velocity, (float) Math.random() * 0.5f + 1);
            }
        }, emissionFrequency);
    }

    public void update(Camera camera) {
        this.transform.position.add(velocity = new Vector2(
                Application.getInstance().getInput().getHorizontalInput(),
                Application.getInstance().getInput().getVerticalInput()).
                mul(Time.deltaTime() * speed));

        camera.transform.position.x = transform.position.x + transform.getScale().x/2;
        camera.transform.position.y = transform.position.y + transform.getScale().x/2;

        Vector2 change = camera.convertToWorldCoordinates(new Vector2(
                Application.getInstance().getInput().getMousePosX(),
                Application.getInstance().getInput().getMousePosY()));

        this.transform.rotation = (float) (Math.atan2(change.y - camera.transform.position.y, change.x - camera.transform.position.x) - Math.PI/2);

        this.scheduler.update();
        this.emitter.position.x = this.transform.position.x + this.transform.getScale().x/2;
        this.emitter.position.y = this.transform.position.y + this.transform.getScale().y/2;
        this.emitter.update();
    }

    public void render(Camera camera, Light light) {
        particleRenderer.begin(camera);
        particleRenderer.renderEmitter(emitter);
        particleRenderer.finish();

        playerRenderer.begin(camera);
        playerRenderer.addLight(light);
        playerRenderer.render(VAO.quad, transform, material);
        playerRenderer.finish();
    }

    public void renderParticles() {

    }

    @Override
    public void dispose() {
        particleRenderer.dispose();
    }

    public Transform getTransform() {
        return transform;
    }
}
