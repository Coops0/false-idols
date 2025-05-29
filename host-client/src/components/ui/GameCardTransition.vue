<template>
  <TransitionGroup name="card" tag="ul">
    <slot/>
  </TransitionGroup>
</template>

<style>
.card-enter-active {
  transition: all 1.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  animation: cardHoverSlam 1.4s ease-out;
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
  transform: scale(0.8) rotate(0deg) translateY(-120px);
  filter: blur(4px);
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

@keyframes cardHoverSlam {
  0% {
    transform: scale(0.8) rotate(0deg) translateY(-120px);
    opacity: 0;
    filter: blur(4px);
    box-shadow: 0 30px 60px rgba(0, 0, 0, 0.4);
  }
  25% {
    transform: scale(0.9) rotate(-2deg) translateY(-80px);
    opacity: 0.6;
    filter: blur(2px);
    box-shadow: 0 25px 50px rgba(0, 0, 0, 0.35);
  }
  45% {
    transform: scale(0.95) rotate(1deg) translateY(-60px);
    opacity: 0.8;
    filter: blur(1px);
    box-shadow: 0 35px 70px rgba(0, 0, 0, 0.4);
  }
  65% {
    transform: scale(1) rotate(-1deg) translateY(-40px);
    opacity: 0.9;
    filter: blur(0px);
    box-shadow: 0 40px 80px rgba(0, 0, 0, 0.45);
  }
  80% {
    transform: scale(1.02) rotate(0deg) translateY(-10px);
    opacity: 0.95;
    box-shadow: 0 45px 90px rgba(0, 0, 0, 0.5);
  }
  90% {
    transform: scale(1.05) rotate(0deg) translateY(5px);
    opacity: 1;
    box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
  }
  95% {
    transform: scale(0.98) rotate(0deg) translateY(-2px);
    box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
  }
  100% {
    transform: scale(1) rotate(0deg) translateY(0);
    opacity: 1;
    filter: blur(0px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
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
  background: radial-gradient(circle, rgba(255, 255, 255, 0.2) 0%, transparent 60%);
  opacity: 0;
  animation: hoverGlow 1.4s ease-out;
  pointer-events: none;
  z-index: 1;
}

@keyframes hoverGlow {
  0% {
    opacity: 0;
  }
  65% {
    opacity: 0.8;
  }
  80% {
    opacity: 1;
  }
  100% {
    opacity: 0;
  }
}

.card-enter-active,
.card-leave-active {
  position: absolute;
  z-index: 10;
}
</style>