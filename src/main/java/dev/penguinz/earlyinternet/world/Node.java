package dev.penguinz.earlyinternet.world;

import dev.penguinz.Sylk.graphics.MainRenderer;
import dev.penguinz.Sylk.graphics.Material;
import dev.penguinz.Sylk.graphics.VAO;
import dev.penguinz.Sylk.logging.Logger;
import dev.penguinz.Sylk.util.Color;
import dev.penguinz.Sylk.util.RefContainer;
import dev.penguinz.Sylk.util.maths.Transform;
import dev.penguinz.Sylk.util.maths.Vector2;
import dev.penguinz.earlyinternet.Assets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class Node {

    private World world;

    private final Transform transform;
    private final Vector2 halfScale;
    private final Material material;

    private final List<Node> connectedNodes = new ArrayList<>();

    public float infection = 0;
    public int timesInfected = 0;
    public int ticksInfected = 0;

    public boolean first = false;

    public int TICKS_INFECTED = (int) (700 + Math.random() * 300);

    public Node(World world, Vector2 position) {
        this.world = world;
        this.transform = new Transform(position, new Vector2(0.2f, 0.2f));
        this.halfScale = new Vector2(transform.getScale().x/2, transform.getScale().y/2);

        this.material = new Material(new RefContainer<>(Assets.getAssets().getComputerTexture()));
        this.material.color = new Color(0, 0, 0);
    }

    public void tick() {
        this.material.color.r = infection;

        if(Math.random() < 0.04d) {
            Vector2 dif = Vector2.sub(world.getGame().getPlayer().getTransform().position, transform.position);
            if (Math.sqrt(dif.x * dif.x + dif.y * dif.y) <= 0.5f) {
                float before = infection;
                infection += (0.05f / ((timesInfected + 1) * (timesInfected + 1)));
                if(before < 1 && infection >= 1) {
                    world.infected++;
                    world.totalInfected++;
                    timesInfected++;
                    TICKS_INFECTED += 6600;
                }
            }
        }

        if(infection >= 1) {
            infection = 1;
            ticksInfected++;
            if(ticksInfected > TICKS_INFECTED && !first) {
                infection = 0;
                world.infected--;
                ticksInfected = 0;
            }
            if(Math.random() > 0.1f)
                return;
            Collections.shuffle(connectedNodes);
            for (Node connectedNode : connectedNodes) {
                boolean infected = connectedNode.infection >= 1;
                float infectionAmount = (world.spreadModifier / (world.antivirusPreventionModifier == 0 ? 1 : ((timesInfected+1)*(timesInfected+1)*world.antivirusPreventionModifier)));
                connectedNode.infection += infectionAmount;
                if(!infected && connectedNode.infection >= 1) {
                    world.infected++;
                    world.totalInfected++;
                    connectedNode.timesInfected++;
                    connectedNode.ticksInfected = 0;
                    TICKS_INFECTED = (int) ((700 + Math.random() * 300) * world.lingeringModifier);
                    // The first node is able to stop being infected once 3% infection percent is reached
                    if(first && world.getInfectionPercent() >= 0.03f) {
                        timesInfected = 1;
                        first = false;
                    }
                }
                world.createCommunication(Vector2.add(transform.position, halfScale), Vector2.add(connectedNode.transform.position, connectedNode.halfScale));
                break;
            }
        }
    }

    public void render(MainRenderer renderer) {
        renderer.render(VAO.quad, transform, material);
    }

    public List<Node> getConnectedNodes() {
        return connectedNodes;
    }
}
