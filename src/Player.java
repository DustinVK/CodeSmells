package com.dustin.main.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.dustin.main.GameManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Player {
    protected GameManager manager;
    private final Vector2 tmp = new Vector2();
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
    private float magnet;

    public float rotation;


    GameManager.SpecialAbility specialAbility;


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
        System.out.println("total mag size: " + magnetSize);
        magnetBounds = new Rectangle(position.x-((magnetSize-size)/2), position.y-((magnetSize-size)/2),magnetSize,magnetSize);
        setSpecialAbility(manager.specialAbility);

    }

    private void getRotation(float x, float y){
        float originX = x;
        float originY = y;
        this.rotation = MathUtils.atan2(originY-position.y, originX-position.x) * MathUtils.radDeg;
    }

    public Texture getTexture() {
        return texture;
    }

    public void update(float x, float y,float dt){
        getRotation(x,y);
        if(specialActive){
            if(TimeUtils.timeSinceMillis(specialTimer)>500){
                specialActive = false;
            }
            float maxDistance = 5*speed * dt;


            //a vector from the player to the touch point:
            tmp.set(dashEnd.x, dashEnd.y).sub(position.x, position.y);

            if (tmp.len() <= size) {// close enough to just set the player at the target
                position.x = dashEnd.x;
                position.y = dashEnd.y;
            } else { // need to move along the vector toward the target
                tmp.nor().scl(maxDistance); //reduce vector length to the distance traveled this frame
                position.x += tmp.x; //move rectangle by the vector length
                position.y += tmp.y;
            }
            bounds.setPosition(position.x, position.y);

            magnetBounds.setPosition(position.x-((magnetSize-size)/2), position.y-((magnetSize-size)/2));
        }
        else {
            //how far the player can move this frame (distance = speed * time):
            float maxDistance = speed * dt;


            //a vector from the player to the touch point:
            tmp.set(x, y).sub(position.x, position.y);


            if (tmp.len() <= 5) {// close enough to just set the player at the target
                position.x = x;
                position.y = y;

            } else { // need to move along the vector toward the target
                tmp.nor().scl(maxDistance); //reduce vector length to the distance traveled this frame

                position.x += tmp.x; //move rectangle by the vector length
                position.y += tmp.y;

            }
            bounds.setPosition(position.x, position.y);
            magnetBounds.setPosition(position.x-((magnetSize-size)/2), position.y-((magnetSize-size)/2));
        }


    }

    private void updateSpecial(){

    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Rectangle getMagnetBounds() {
        return magnetBounds;
    }

    public void setSpecialAbility(GameManager.SpecialAbility special){
        specialAbility = special;

        switch (specialAbility){
            case DASH:
                break;

            case BOOST:
                break;

            case ANTIBODY:
                break;
        }
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