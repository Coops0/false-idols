<template>
  <div>
    <input v-model="name"/>
    <button @click="join">{{ isRejoin ? 'Rejoin' : 'Join' }}</button>
  </div>
</template>

<script lang="ts" setup>
import { isNameValid } from '@/util';

defineProps<{ isRejoin: boolean; }>();
const name = defineModel<string>({ required: true });
const emit = defineEmits<{ join: [] }>();

function join() {
  if (isNameValid(name.value)) {
    emit('join');
  }
}
</script>