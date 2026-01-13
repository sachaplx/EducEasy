<template>
  <v-dialog v-model="open" max-width="640">
    <v-card rounded="xl">
      <v-card-title class="d-flex align-center justify-space-between">
        <span class="text-h6 font-weight-bold">Ajouter un élève</span>
        <v-btn icon variant="text" @click="open = false" aria-label="Fermer">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>

      <v-card-text>
        <v-form ref="formRef" @submit.prevent="onSubmit">
          <v-row dense>
            <v-col cols="12">
              <v-text-field
                v-model="form.nom"
                label="Nom"
                variant="outlined"
                :rules="[rules.required]"
                autocomplete="off"
              />
            </v-col>
            <v-col cols="12">
              <v-text-field
                v-model="form.prenom"
                label="Prénom"
                variant="outlined"
                :rules="[rules.required]"
                autocomplete="off"
              />
            </v-col>
            <v-col cols="12">
              <v-select
                v-model="form.gender"
                :items="genderItems"
                item-title="label"
                item-value="value"
                label="Genre"
                variant="outlined"
              />
            </v-col>
          </v-row>

          <div class="d-flex justify-end mt-4" style="gap: 8px">
            <v-btn variant="text" @click="open = false">Annuler</v-btn>
            <v-btn
              type="submit"
              color="primary"
              :loading="loading"
              :disabled="disabled"
            >
              Ajouter
            </v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { reactive, ref, watch, computed } from "vue";

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  loading: { type: Boolean, default: false },
  classroomId: { type: Number, default: null },
});

const emit = defineEmits(["update:modelValue", "submit"]);

const open = ref(props.modelValue);
watch(
  () => props.modelValue,
  (v) => (open.value = v)
);
watch(open, (v) => emit("update:modelValue", v));

const disabled = computed(() => !props.classroomId);

const formRef = ref(null);
const form = reactive({ nom: "", prenom: "", gender: "BOY" });

const rules = {
  required: (v) => (!!v && String(v).trim().length > 0) || "Champ requis",
};

const genderItems = [
  { label: "Garçon", value: "BOY" },
  { label: "Fille", value: "GIRL" },
];

watch(open, (v) => {
  if (!v) return;
  form.nom = "";
  form.prenom = "";
  form.gender = "BOY";
});

async function onSubmit() {
  if (props.loading || disabled.value) return;
  const res = await formRef.value?.validate?.();
  if (res && res.valid === false) return;

  emit("submit", {
    classroomId: props.classroomId,
    nom: form.nom,
    prenom: form.prenom,
    gender: form.gender,
  });
}
</script>
