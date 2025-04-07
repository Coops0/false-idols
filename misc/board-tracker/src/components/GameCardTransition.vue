<template>
  <TransitionGroup name="card" tag="ul">
    <slot/>
  </TransitionGroup>
</template>

<style>
.card-enter-active, .card-leave-active, .card-move {
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.card-enter-from, .card-leave-to {
  opacity: 0;
  transform: scale(3);
}

.card-enter-to {
  opacity: 1;
  transform: scale(1);
}
</style>