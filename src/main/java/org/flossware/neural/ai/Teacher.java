package org.flossware.neural.ai;

public interface Teacher {
    TeachingResponse teach(TeachingRequest request);
}
