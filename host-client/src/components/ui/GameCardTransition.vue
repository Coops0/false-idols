<template>
  <TransitionGroup name="card" tag="ul">
    <slot/>
  </TransitionGroup>
</template>

<style>
.card-enter-active {
  transition: all 1.2s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  animation: cardDrop 1.2s ease-out;
}

.card-leave-active {
  transition: all 0.8s cubic-bezier(0.55, 0.055, 0.675, 0.19);
  animation: cardFlyOut 0.8s ease-in;
}

.card-move {
  transition: all 0.9s cubic-bezier(0.23, 1, 0.32, 1);
}

.card-enter-from {
  opacity: 0;
  transform: scale(0.3) rotate(-180deg) translateY(-200px);
  filter: blur(8px);
}

.card-enter-to {
  opacity: 1;
  transform: scale(1) rotate(0deg) translateY(0);
  filter: blur(0px);
}

.card-leave-to {
  opacity: 0;
  transform: scale(0.1) rotate(90deg) translateX(300px);
  filter: blur(4px);
}

@keyframes cardDrop {
  0% {
    transform: scale(0.3) rotate(-180deg) translateY(-200px);
    opacity: 0;
    filter: blur(8px);
    box-shadow: 0 0 0 rgba(0, 0, 0, 0);
  }
  20% {
    transform: scale(0.6) rotate(-90deg) translateY(-100px);
    opacity: 0.5;
    filter: blur(4px);
  }
  50% {
    transform: scale(1.1) rotate(-20deg) translateY(10px);
    opacity: 0.8;
    filter: blur(1px);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  }
  70% {
    transform: scale(1.05) rotate(5deg) translateY(-5px);
    opacity: 0.95;
    filter: blur(0px);
    box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
  }
  85% {
    transform: scale(0.98) rotate(-2deg) translateY(2px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
  }
  100% {
    transform: scale(1) rotate(0deg) translateY(0);
    opacity: 1;
    filter: blur(0px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  }
}

@keyframes cardFlyOut {
  0% {
    transform: scale(1) rotate(0deg) translateX(0);
    opacity: 1;
    filter: blur(0px);
  }
  20% {
    transform: scale(1.1) rotate(-10deg) translateX(50px);
    opacity: 0.9;
  }
  60% {
    transform: scale(0.5) rotate(45deg) translateX(200px);
    opacity: 0.4;
    filter: blur(2px);
  }
  100% {
    transform: scale(0.1) rotate(90deg) translateX(300px);
    opacity: 0;
    filter: blur(4px);
  }
}

.card-enter-active::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
  opacity: 0;
  animation: shimmer 1.2s ease-out;
  pointer-events: none;
  z-index: 1;
}

@keyframes shimmer {
  0% {
    opacity: 0;
  }
  30% {
    opacity: 1;
  }
  100% {
    opacity: 0;
  }
}

.card-enter-active,
.card-leave-active {
  position: relative;
  z-index: 10;
}
</style>