<template>
  <v-dialog v-model="open" max-width="520">
    <v-card>
      <v-card-title>Ajouter une absence</v-card-title>
      <v-card-text>
        <v-form ref="form" v-model="valid" validate-on="submit">
          <v-text-field
            v-model="formData.date"
            label="Date"
            type="date"
            :rules="[r.required]"
          />
          <v-select
            v-model="formData.halfDay"
            :items="halfDays"
            label="Demi-journée"
            :rules="[r.required]"
          />
          <v-switch
            v-model="formData.justifie"
            label="Justifiée ?"
            color="primary"
            hide-details
          />
          <v-textarea
            v-model="formData.motif"
            label="Motif (optionnel)"
            rows="3"
            auto-grow
          />
        </v-form>
      </v-card-text>
      <v-card-actions class="justify-end">
        <v-btn variant="text" @click="close">Annuler</v-btn>
        <v-btn color="primary" :loading="loading" @click="submit"
          >Enregistrer</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, watch } from "vue";
import { api } from "@/services/api";

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  pupilId: { type: Number, required: true },
});
const emit = defineEmits(["update:modelValue", "saved"]);

const open = ref(props.modelValue);
watch(
  () => props.modelValue,
  (v) => (open.value = v)
);
watch(open, (v) => emit("update:modelValue", v));

const loading = ref(false);
const valid = ref(false);
const form = ref(null);

const halfDays = [
  { title: "Matin", value: "MORNING" },
  { title: "Après-midi", value: "AFTERNOON" },
  { title: "Journée", value: "FULL" },
];

const formData = ref({
  date: new Date().toISOString().slice(0, 10),
  halfDay: "MORNING",
  justifie: false,
  motif: "",
});

const r = { required: (v) => !!(v || v === 0) || "Requis" };
function close() {
  open.value = false;
}

async function submit() {
  const ok = await form.value?.validate();
  if (!ok?.valid) return;
  loading.value = true;
  try {
    await api.post(`/pupils/${props.pupilId}/absences/add`, formData.value);
    emit("saved");
    close();
  } finally {
    loading.value = false;
  }
}
</script>
