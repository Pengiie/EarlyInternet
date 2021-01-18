package dev.penguinz.earlyinternet.world;

import dev.penguinz.Sylk.Camera;
import dev.penguinz.Sylk.OrthographicCamera;
import dev.penguinz.Sylk.graphics.MainRenderer;
import dev.penguinz.Sylk.graphics.Material;
import dev.penguinz.Sylk.graphics.RenderLayer;
import dev.penguinz.Sylk.graphics.VAO;
import dev.penguinz.Sylk.particles.Particle;
import dev.penguinz.Sylk.particles.ParticleBuilder;
import dev.penguinz.Sylk.particles.ParticleEmitter;
import dev.penguinz.Sylk.particles.ParticleRenderer;
import dev.penguinz.Sylk.tasks.Task;
import dev.penguinz.Sylk.tasks.TaskScheduler;
import dev.penguinz.Sylk.util.Color;
import dev.penguinz.Sylk.util.Disposable;
import dev.penguinz.Sylk.util.maths.Transform;
import dev.penguinz.Sylk.util.maths.Vector2;
import dev.penguinz.earlyinternet.Game;

import java.util.ArrayList;
import java.util.List;

public class World implements Disposable {

    private Game game;

    private int ticks = 1000;
    private final TaskScheduler ticker;

    private List<Node> nodes = new ArrayList<>();

    private Vector2 spawnPoint;

    private ParticleRenderer particleRenderer;
    private ParticleEmitter particleEmitter;
    private ParticleBuilder communicationParticle = new ParticleBuilder().
            setColor(new Color(0.8f, 0.2f, 0.3f)).
            setSize(new Vector2(0.1f, 0.1f)).
            setRotation(0);

    private final float SPEED = 20f;

    public int infected = 0;
    public int totalInfected = 0;

    private Task tickingTask;

    public float antivirusPreventionModifier = 1;
    public float spreadModifier = 0.1f;
    public float lingeringModifier = 1.0f;

    public World(Game game) {
        this.game = game;
        this.ticker = new TaskScheduler();
        this.tickingTask = this.ticker.scheduleRepeatingTask(this::tick, (float) 1 / ticks);

        this.particleRenderer = new ParticleRenderer(RenderLayer.RENDER0);
        this.particleEmitter = new ParticleEmitter(new Vector2());

        generate();
    }

    private void generate() {
        // Create Nodes
        final int MIN = 500, RANDOMNESS = 2000;
        final float SPREAD = 1f, DEVIATION = 0.35f;

        int nodeCount = (int) (Math.random() * RANDOMNESS + MIN);

        float averageX = 0, averageY = 0;
        Vector2 position = new Vector2();
        for (int i = 0; i < nodeCount; i++) {
            position.add(new Vector2(
                    ((float) Math.random() * DEVIATION + SPREAD) * (Math.random() > 0.5f ? 1 : -1),
                    ((float) Math.random() * DEVIATION + SPREAD) * (Math.random() > 0.5f ? 1 : -1)));
            nodes.add(new Node(this, new Vector2(position)));
            averageX += position.x;
            averageY += position.y;
        }

        this.spawnPoint = new Vector2(averageX/nodeCount, averageY/nodeCount);

        // Create Connections
        final int MIN_CONNECTIONS = 3, DEVIATION_CONNECTIONS = 7;

        for (Node node : nodes) {
            for (int i = 0; i < Math.random() * DEVIATION_CONNECTIONS + MIN_CONNECTIONS; i++) {
                Node randomNode;
                // Should rather make a list that gets picked from cause technically the O(n) on this is highly variable and can technically run for infinity
                while (node.getConnectedNodes().contains(randomNode = nodes.get((int) (Math.random() * (nodes.size() - 1)))));
                node.getConnectedNodes().add(randomNode);
                if(!randomNode.getConnectedNodes().contains(node)) randomNode.getConnectedNodes().add(node);
            }
        }

//        Node first = nodes.get((int) (Math.random() * (nodes.size() - 1)));
//        first.infection = 1;
//        first.first = true;
    }

    public void tick() {
        nodes.forEach(Node::tick);
    }

    public void update() {
        ticker.update();
        this.particleEmitter.update();
    }

    public void createCommunication(Vector2 source, Vector2 receiver) {
        this.particleEmitter.position = source;
        Vector2 change = new Vector2(receiver.x - source.x, receiver.y - source.y);
        Particle particle = communicationParticle.setRotation((float) (Math.atan2(change.y, change.x) - Math.PI/2)).build();
        Vector2 velocity = Vector2.normalize(change).mul(SPEED);
        float lifetime = (float) Math.sqrt(change.x * change.x + change.y * change.y) /
                (float) Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y);
        this.particleEmitter.emit(particle, velocity, lifetime);
    }

    public void render(MainRenderer renderer, OrthographicCamera camera) {
        renderer.render(VAO.quad, new Transform(new Vector2(-10000, -10000), new Vector2(20000, 20000)), new Material(new Color(0.9f, 0.9f, 0.9f)));
        nodes.forEach(node -> node.render(renderer));
    }

    public void renderCommunications(Camera camera) {
        particleRenderer.begin(camera);
        particleRenderer.renderEmitter(this.particleEmitter);
        particleRenderer.flush();
    }

    public float getInfectionPercent() {
        return (float) infected / nodes.size();
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public Vector2 getSpawnPoint() {
        return spawnPoint;
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
        tickingTask.cancel();
        this.tickingTask = this.ticker.scheduleRepeatingTask(this::tick, (float) 1 / ticks);
    }

    public Game getGame() {
        return game;
    }

    @Override
    public void dispose() {
        this.particleRenderer.dispose();
    }
}
