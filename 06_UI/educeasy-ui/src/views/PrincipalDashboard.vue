<template>
  <v-container class="py-6">
    <v-row dense justify="center">
      <v-col cols="12" md="8">
        <v-card>
          <v-card-title class="d-flex align-center justify-space-between">
            <div class="text-h6">Classes par école</div>
            <template #loader>
              <div class="d-flex flex-column py-1" style="width: 180px">
                <v-skeleton-loader type="list-item-two-line" class="mb-1" />
                <v-skeleton-loader type="list-item-two-line" class="mb-1" />
                <v-skeleton-loader type="list-item-two-line" />
              </div>
            </template>
            <v-tooltip text="Actualiser les écoles" location="top">
              <template #activator="{ props }">
                <v-btn
                  v-bind="props"
                  icon
                  variant="text"
                  density="comfortable"
                  :disabled="loading.schools"
                  @click="loadEcoles"
                  aria-label="Actualiser"
                  :loading="loading.schools"
                >
                  <v-icon>mdi-refresh</v-icon>
                </v-btn>
              </template>
            </v-tooltip>
          </v-card-title>
          <v-card-text>
            <div v-if="!ecoles.length" class="text-medium-emphasis">
              Charge les écoles pour commencer.
            </div>

            <template v-else>
              <v-select
                v-model="selectedEcoleId"
                :items="ecoles"
                item-title="nom"
                item-value="id"
                label="École"
                variant="outlined"
                @update:modelValue="loadClasses"
                hide-details="auto"
                class="mb-4"
              />
              <div class="d-flex justify-end mt-n3 mb-1" v-if="selectedEcoleId">
                <v-tooltip text="Actualiser les classes" location="top">
                  <template #activator="{ props }">
                    <v-btn
                      v-bind="props"
                      size="small"
                      icon
                      variant="text"
                      :disabled="loading.classes"
                      @click="loadClasses"
                      aria-label="Actualiser classes"
                    >
                      <v-icon :class="{ 'mdi-spin': loading.classes }">
                        mdi-refresh
                      </v-icon>
                    </v-btn>
                  </template>
                </v-tooltip>
              </div>
              <v-alert v-if="error" type="error" variant="tonal" class="mt-2">{{
                error
              }}</v-alert>

              <v-expansion-panels
                v-if="classes.length"
                v-model="opened"
                multiple
                variant="accordion"
                class="mt-3"
                @update:modelValue="onExpanded"
              >
                <v-expansion-panel
                  v-for="c in classes"
                  :key="c.id"
                  :value="c.id"
                  class="mb-2"
                >
                  <v-expansion-panel-title>
                    <div
                      class="d-flex align-center justify-space-between w-100"
                    >
                      <div class="d-flex align-center" style="gap: 10px">
                        <v-icon>mdi-google-classroom</v-icon>
                        <span class="text-subtitle-1">
                          {{ c.name || c.nom || "Classe #" + c.id }}
                        </span>
                      </div>
                      <div class="d-flex align-center" style="gap: 8px">
                        <v-chip
                          v-if="c.teacherLastName || c.teacherFirstName"
                          size="small"
                          color="primary"
                          variant="tonal"
                        >
                          {{ c.teacherLastName.toUpperCase() }}
                          {{ c.teacherFirstName }}
                        </v-chip>
                        <v-chip
                          v-if="state(c.id).loaded"
                          size="small"
                          variant="tonal"
                        >
                          {{ state(c.id).items.length }} élèves
                        </v-chip>
                      </div>
                    </div>
                  </v-expansion-panel-title>

                  <v-expansion-panel-text>
                    <div
                      v-if="state(c.id).loading"
                      class="py-4 d-flex justify-center"
                    >
                      <v-progress-circular indeterminate />
                    </div>

                    <v-alert
                      v-else-if="state(c.id).error"
                      type="error"
                      variant="tonal"
                      class="my-2"
                    >
                      {{ state(c.id).error }}
                    </v-alert>

                    <div v-else>
                      <v-text-field
                        v-model="filters[c.id]"
                        label="Filtrer les élèves"
                        variant="outlined"
                        density="compact"
                        hide-details="auto"
                        class="mb-2"
                        prepend-inner-icon="mdi-magnify"
                      />
                      <v-list lines="two">
                        <v-list-item
                          v-for="p in filteredPupils(c.id)"
                          :key="p.id"
                          :title="`${p.prenom} ${p.nom}`"
                          @click="goToPupil(p)"
                          class="rounded"
                          density="comfortable"
                        >
                          <template #prepend>
                            <v-avatar :color="avatarColor(p.gender)" size="32">
                              <span class="avatar-initials">{{
                                initials(p.prenom, p.nom)
                              }}</span>
                            </v-avatar>
                          </template>
                          <template #append>
                            <v-btn
                              size="small"
                              variant="text"
                              icon="mdi-open-in-new"
                              @click.stop="goToPupil(p)"
                            />
                          </template>
                        </v-list-item>

                        <div
                          v-if="!state(c.id).items.length"
                          class="text-medium-emphasis text-center py-6"
                        >
                          Aucun élève dans cette classe.
                        </div>
                      </v-list>
                    </div>
                  </v-expansion-panel-text>
                </v-expansion-panel>
              </v-expansion-panels>

              <div
                v-else-if="!loading.classes"
                class="text-medium-emphasis mt-3"
              >
                Aucune classe pour cette école.
              </div>
            </template>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";
import { api } from "../services/api";

const router = useRouter();
const auth = useAuthStore();

const ecoles = ref([]);
const classes = ref([]);
const selectedEcoleId = ref(null);
const loading = ref({ schools: false, classes: false });
const error = ref("");

const opened = ref([]);
const byClass = reactive({});
const filters = reactive({});

onMounted(loadEcoles);

function state(classId) {
  if (!byClass[classId])
    byClass[classId] = {
      loading: false,
      loaded: false,
      error: null,
      items: [],
    };
  if (filters[classId] === undefined) filters[classId] = "";
  return byClass[classId];
}

async function loadEcoles() {
  if (loading.value.schools) return;
  loading.value.schools = true;
  error.value = "";
  try {
    const { data } = await api.get("/schools/mine", {
      params: { _ts: Date.now() },
    });
    ecoles.value = Array.isArray(data) ? data : [];
  } catch (e) {
    error.value = "Impossible de charger vos écoles.";
  } finally {
    loading.value.schools = false;
  }
}

async function loadClasses() {
  if (!selectedEcoleId.value) return;
  loading.value.classes = true;
  error.value = "";
  try {
    const { data } = await api.get(
      `/classrooms/${selectedEcoleId.value}/list`,
      {
        params: { _ts: Date.now() },
      }
    );
    classes.value = Array.isArray(data) ? data : [];

    opened.value = [];
    for (const k of Object.keys(byClass)) delete byClass[k];
    for (const k of Object.keys(filters)) delete filters[k];
  } catch (e) {
    error.value = "Impossible de charger les classes.";
  } finally {
    loading.value.classes = false;
  }
}

async function loadPupils(classId) {
  const s = state(classId);
  if (s.loaded || s.loading) return;
  s.loading = true;
  s.error = null;
  try {
    const { data } = await api.get(`/classrooms/${classId}/list/pupils`);
    s.items = Array.isArray(data)
      ? data
      : Array.isArray(data?.pupils)
      ? data.pupils
      : [];
    s.loaded = true;
  } catch (e) {
    s.error = "Chargement des élèves impossible.";
  } finally {
    s.loading = false;
  }
}

function onExpanded(newVals) {
  const ids = Array.isArray(newVals) ? newVals : [newVals];
  ids.forEach((id) => loadPupils(id));
}

function initials(firstName, lastName) {
  const f = (firstName || "").trim().charAt(0).toUpperCase();
  const l = (lastName || "").trim().charAt(0).toUpperCase();
  return f + l || "??";
}

function avatarColor(gender) {
  if (String(gender).toUpperCase() === "GIRL") return "pink";
  if (String(gender).toUpperCase() === "BOY") return "blue";
  return "grey";
}

function filteredPupils(classId) {
  const s = state(classId);
  const q = (filters[classId] || "").trim().toLowerCase();
  if (!q) return s.items;
  return s.items.filter(
    (p) =>
      (p.nom || "").toLowerCase().includes(q) ||
      (p.prenom || "").toLowerCase().includes(q)
  );
}

function subtitlePupil(p) {
  if (p.classeNom && p.ecoleNom) return `${p.classeNom} — ${p.ecoleNom}`;
  if (p.classeNom) return p.classeNom;
  return "Élève";
}

function goToPupil(p) {
  router.push({ name: "pupil-profile", params: { id: p.id } });
}
</script>
