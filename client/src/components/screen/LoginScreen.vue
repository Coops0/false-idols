<template>
  <div>
    <input v-model="name"/>
    <button @click="() => emit('join')">{{ isRejoin ? 'Rejoin' : 'Join' }}</button>
  </div>
</template>

<script lang="ts" setup>
defineProps<{ isRejoin: boolean; }>();
const name = defineModel<string>({ required: true });
const emit = defineEmits<{ join: [] }>();
</script>