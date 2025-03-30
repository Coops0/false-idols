<template>
  <div class="fixed bottom-0 right-0 p-4">
    <Transition name="toast">
      <div v-if="show" class="text-white p-4 rounded-lg shadow-lg" :class="color">
        <slot/>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  show: boolean;
  color: string;
}>();
</script>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>