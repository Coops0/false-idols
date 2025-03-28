<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100 p-4">
    <BaseCard class="w-full max-w-md">
      <div class="space-y-4">
        <div>
          <input
              v-model="name"
              class="w-full px-4 py-2 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-colors"
              placeholder="Name"
              type="text"
              @keydown.enter="join"
          />
        </div>

        <BaseButton
            :disabled="!isNameValid(name)"
            class="w-full"
            variant="primary"
            @click="join"
        >
          {{ isRejoin ? 'Rejoin' : 'Join' }}
        </BaseButton>
      </div>
    </BaseCard>
  </div>
</template>

<script lang="ts" setup>
import { isNameValid } from '@/util';
import BaseButton from '@/components/ui/BaseButton.vue';
import BaseCard from '@/components/ui/BaseCard.vue';

defineProps<{ isRejoin: boolean; }>();
const name = defineModel<string>({ required: true });
const emit = defineEmits<{ join: [] }>();

function join() {
  if (isNameValid(name.value)) {
    emit('join');
  }
}
</script>