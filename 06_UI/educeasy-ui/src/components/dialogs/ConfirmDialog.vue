<template>
  <v-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    max-width="480"
    persistent
  >
    <v-card>
      <v-card-title class="d-flex align-center" style="gap: 12px">
        <v-icon :color="iconColor" size="28">{{ icon }}</v-icon>
        <span class="text-h6">{{ title }}</span>
      </v-card-title>

      <v-card-text class="text-body-1">
        {{ message }}
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn variant="text" @click="handleCancel" :disabled="loading">
          {{ cancelText }}
        </v-btn>
        <v-btn
          :color="confirmColor"
          :variant="confirmVariant"
          :loading="loading"
          @click="handleConfirm"
        >
          <v-icon v-if="confirmIcon" start>{{ confirmIcon }}</v-icon>
          {{ confirmText }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
const props = defineProps({
  modelValue: { type: Boolean, required: true },
  title: { type: String, default: "Confirmer l'action" },
  message: { type: String, default: "Êtes-vous sûr de vouloir continuer ?" },
  confirmText: { type: String, default: "Confirmer" },
  cancelText: { type: String, default: "Annuler" },
  confirmColor: { type: String, default: "primary" },
  confirmVariant: { type: String, default: "flat" },
  confirmIcon: { type: String, default: "" },
  icon: { type: String, default: "mdi-help-circle" },
  iconColor: { type: String, default: "primary" },
  loading: { type: Boolean, default: false },
  type: {
    type: String,
    default: "default",
    validator: (v) => ["default", "danger", "warning", "info"].includes(v),
  },
});

const emit = defineEmits(["update:modelValue", "confirm", "cancel"]);

// Computed pour adapter les couleurs selon le type
const iconColor = computed(() => {
  if (props.type === "danger") return "error";
  if (props.type === "warning") return "warning";
  if (props.type === "info") return "info";
  return props.iconColor;
});

const icon = computed(() => {
  if (props.type === "danger") return "mdi-alert-circle";
  if (props.type === "warning") return "mdi-alert";
  if (props.type === "info") return "mdi-information";
  return props.icon;
});

const confirmColor = computed(() => {
  if (props.type === "danger") return "error";
  if (props.type === "warning") return "warning";
  return props.confirmColor;
});

function handleConfirm() {
  emit("confirm");
}

function handleCancel() {
  emit("cancel");
  emit("update:modelValue", false);
}
</script>

<script>
import { computed } from "vue";
export default { name: "ConfirmDialog" };
</script>
