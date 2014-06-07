package com.lostcodestudios.theliving;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class TheLiving extends ApplicationAdapter {
	private static final float TIME_STEP = 1 / 60.0f;
	private static final int VELOCITY_ITERATIONS = 6;
	private static final int POSITION_ITERATIONS = 2;
	
	SpriteBatch batch;
	Texture img;
	
	World world;
	Body body;
	RayHandler handler;
	
	Camera camera;
	
	float timeToStep = 0f;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		
		camera = new OrthographicCamera(640, 480);
		
		world = new World(new Vector2(), true);
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(new Vector2(img.getWidth() / 2, img.getHeight() / 2));
		body = world.createBody(bodyDef);
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(img.getWidth() / 2, img.getHeight() / 2);
		fixtureDef.shape = shape;
		
		body.createFixture(fixtureDef);
		shape.dispose();
		
		handler = new RayHandler(world);
		
		handler.setAmbientLight(0, 0.5f, 0, 0.1f);
		
		
		new PointLight(handler, 10, Color.RED, 256f, 128f, 6f);
	}

	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		timeToStep += delta;
		
		Vector2 velocity = new Vector2();
		
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			velocity.x = -1.0f;
		}

		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			velocity.x = 1.0f;
		}
		
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			velocity.y = 1.0f;
		}
			
		if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			velocity.y = -1.0f;
		}

		velocity.nor();
		velocity.scl(5000.0f * delta);
		
		body.setLinearVelocity(velocity);
		
		while (timeToStep >= TIME_STEP) {
			world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
			timeToStep -= TIME_STEP;
		}
		
		Gdx.gl.glClearColor(0, 0, 0.4f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.getProjectionMatrix().set(camera.combined);
		batch.begin();
		batch.draw(img, body.getPosition().x - img.getWidth() / 2, body.getPosition().y - img.getHeight() / 2);
		batch.end();
		
		handler.setCombinedMatrix(camera.combined);
		handler.updateAndRender();

		Gdx.graphics.setTitle("Light at img: " + handler.pointAtLight(body.getPosition().x, body.getPosition().y));
	}
}