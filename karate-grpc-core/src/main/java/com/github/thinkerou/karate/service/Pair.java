package com.github.thinkerou.karate.service;

import java.util.Objects;

public final class Pair<L,R> {

  private final L left;
  private final R right;

  public Pair(L left, R right) {
    this.left = Objects.requireNonNull(left, "Value cannot be null");
    this.right = Objects.requireNonNull(right, "Value cannot be null");
  }

  public L left() {
    return left;
  }

  public R right() {
    return right;
  }
}
