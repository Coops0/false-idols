<template>
  <div class="fixed bottom-0 right-0 p-4">
    <Transition name="toast" mode="out-in">
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