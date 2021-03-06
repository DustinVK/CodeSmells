package com.dustin.main.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.dustin.main.GameManager;

public class Player {
    protected GameManager manager;
    private final Vector2 movementVector = new Vector2();
    private Rectangle bounds;
    public Rectangle magnetBounds;
    public Vector3 position;
    public float size;
    public float magnetSize;
    public Texture texture = new Texture("imp-tcell.png");
    public Texture face = new Texture("imp-face.png");
    public TextureRegion region = new TextureRegion(face);
    public float speed;   //world units per second
    private boolean specialActive;
    private long specialTimer;
    private Vector2 dashEnd;

    public float rotation;

    public Player(float x, float y, GameManager manager) {
        this.manager = manager;
        position = new Vector3(x, y, 0);
        specialActive = false;
        this.rotation = 0;
        updatePlayer();
    }

    public void updatePlayer(){
        size = manager.getPlayerSize();
        speed = manager.getPlayerSpeed();
        bounds = new Rectangle(position.x, position.y, size, size);
        magnetSize = size * 1.5f * manager.getPlayerMagnet();
        magnetBounds = new Rectangle(position.x-((magnetSize-size)/2), position.y-((magnetSize-size)/2),magnetSize,magnetSize);
    }

    private void getRotation(float x, float y){
        this.rotation = MathUtils.atan2(y-position.y, x-position.x) * MathUtils.radDeg;
    }

    public Texture getTexture() {
        return texture;
    }

    public void update(float x, float y,float dt){
        getRotation(x,y);
        if(specialActive){
            updateSpecial(dt);
        }
        else {
            updateNormal(x, y, dt);
        }
    }

    //how far the player can move this frame (distance = speed * time):
    private float maxDistance(float dt){
        return speed * dt;
    }

    private void updateNormal(float x, float y, float dt) {
        //a vector from the player to the touch point:
        setMovementVector(x, y);
        normalizeMovement(x, y, maxDistance(dt), 5);
        bounds.setPosition(position.x, position.y);
        magnetBounds.setPosition(position.x-((magnetSize-size)/2), position.y-((magnetSize-size)/2));
    }

    private void setMovementVector(float x, float y) {
        movementVector.set(x, y).sub(position.x, position.y);
    }

    private void normalizeMovement(float x, float y, float maxDistance, float i) {
        if (movementVector.len() <= i) {// close enough to just set the player at the target
            position.x = x;
            position.y = y;

        } else { // need to move along the vector toward the target
            movementVector.nor().scl(maxDistance); //reduce vector length to the distance traveled this frame
            position.x += movementVector.x; //move rectangle by the vector length
            position.y += movementVector.y;
        }
    }

    private void updateSpecial(float dt) {
        if(TimeUtils.timeSinceMillis(specialTimer)>500){
            specialActive = false;
        }
        float maxDistance = 5*speed * dt;
        setMovementVector(dashEnd.x, dashEnd.y);
        normalizeMovement(dashEnd.x, dashEnd.y, maxDistance, size);
        bounds.setPosition(position.x, position.y);
        magnetBounds.setPosition(position.x-((magnetSize-size)/2), position.y-((magnetSize-size)/2));
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Rectangle getMagnetBounds() {
        return magnetBounds;
    }

    public void activateSpecial(float x, float y){
        specialActive = true;
        specialTimer = TimeUtils.millis();
        dashEnd = new Vector2(x,y);
    }

    public void dispose() {
        texture.dispose();
    }
}