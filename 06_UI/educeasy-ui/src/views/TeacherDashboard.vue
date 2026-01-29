<template>
  <v-container fluid class="dashboard">
    <div class="dashboard__inner" style="gap: 12px">
      <div>
        <div class="text-h4 font-weight-bold">Bonjour, {{ displayName }}</div>
        <div class="text-body-2 text-medium-emphasis">
          Voici un aperçu de vos classes aujourd'hui
        </div>
      </div>

      <v-alert v-if="error" type="error" variant="tonal" class="mb-4">
        {{ error }}
      </v-alert>

      <v-row dense>
        <!-- Left: My Classes -->
        <v-col cols="12" md="8">
          <v-card rounded="xl" class="pa-4">
            <div
              class="d-flex flex-wrap align-center justify-space-between"
              style="gap: 12px"
            >
              <div>
                <div class="text-h6 font-weight-bold">Mes classes</div>
                <div class="text-body-2 text-medium-emphasis">
                  Gérez vos élèves et suivez leur progression
                </div>
              </div>

              <v-tooltip text="Actualiser" location="top">
                <template #activator="{ props }">
                  <v-btn
                    v-bind="props"
                    icon
                    variant="text"
                    :loading="school.loading.classrooms"
                    @click="loadMyClasses"
                    aria-label="Actualiser"
                  >
                    <v-icon>mdi-refresh</v-icon>
                  </v-btn>
                </template>
              </v-tooltip>
            </div>

            <div class="mt-4">
              <template v-if="school.loading.classrooms">
                <v-row dense>
                  <v-col v-for="i in 2" :key="i" cols="12" sm="6">
                    <v-skeleton-loader type="card" />
                  </v-col>
                </v-row>
              </template>

              <template v-else-if="!classes.length">
                <v-card
                  rounded="lg"
                  class="pa-6 text-center"
                  color="blue-lighten-5"
                  variant="flat"
                >
                  <v-icon size="48" color="primary" class="mb-3"
                    >mdi-information-outline</v-icon
                  >
                  <div class="text-h6 font-weight-bold mb-2">
                    Aucune classe attribuée
                  </div>
                  <div class="text-body-2 text-medium-emphasis mb-3">
                    Vous n'avez pas encore de classe assignée. L'attribution des
                    classes est gérée par le directeur de votre établissement.
                  </div>
                  <div
                    class="text-body-2 font-weight-medium"
                    style="color: primary"
                  >
                    Contactez votre directeur pour obtenir l'accès à vos
                    classes.
                  </div>
                </v-card>
              </template>

              <template v-else>
                <div class="class-grid">
                  <ClassCards
                    v-for="c in classes"
                    :key="c.id"
                    :name="classLabel(c)"
                    :level="cycleLabel(c)"
                    :student-count="pupilsCount(c)"
                    :course-count="coursesCount(c)"
                    :color="pickColor(c)"
                    @click="openClass(c)"
                  />
                </div>
              </template>
            </div>
          </v-card>
        </v-col>

        <!-- Right: Quick Stats -->
        <v-col cols="12" md="4">
          <PresenceCard
            class="mb-4"
            :value="presenceRateDisplay"
            subtitle="Cette semaine"
            :loading="loading.presence"
            :error="presenceError"
            @refresh="loadPresence"
          />
        </v-col>
      </v-row>

      <!-- Dialog: pupils in class -->
      <v-dialog v-model="pupilsDialog" max-width="720">
        <v-card rounded="xl">
          <v-card-title
            class="d-flex align-center"
            style="gap: 8px; flex-wrap: nowrap"
          >
            <div
              class="d-flex flex-column"
              style="flex: 1; min-width: 0; overflow: hidden"
            >
              <span
                class="text-h6 font-weight-bold"
                style="
                  overflow: hidden;
                  text-overflow: ellipsis;
                  white-space: nowrap;
                "
                >{{ classLabel(activeClass) }}</span
              >
              <span
                class="text-body-2 text-medium-emphasis"
                style="
                  overflow: hidden;
                  text-overflow: ellipsis;
                  white-space: nowrap;
                "
              >
                {{ cycleLabel(activeClass) }}
              </span>
            </div>

            <div class="d-flex align-center" style="gap: 6px; flex-shrink: 0">
              <v-btn
                icon
                variant="text"
                :loading="loading.pupils"
                @click="refreshPupils"
                aria-label="Actualiser élèves"
              >
                <v-icon>mdi-refresh</v-icon>
              </v-btn>
              <v-btn
                icon
                variant="text"
                @click="pupilsDialog = false"
                aria-label="Fermer"
              >
                <v-icon>mdi-close</v-icon>
              </v-btn>
            </div>
          </v-card-title>

          <v-card-text>
            <v-text-field
              v-model="pupilQuery"
              label="Rechercher un élève"
              variant="outlined"
              density="comfortable"
              hide-details
              prepend-inner-icon="mdi-magnify"
              class="mb-3"
            />

            <div v-if="loading.pupils" class="py-8 d-flex justify-center">
              <v-progress-circular indeterminate />
            </div>

            <div
              v-else-if="!pupils.length"
              class="text-medium-emphasis py-8 text-center"
            >
              Aucun élève dans cette classe.
            </div>

            <v-list v-else lines="two" density="comfortable">
              <v-list-item
                v-for="p in pupilsFiltered"
                :key="p.id"
                class="rounded mb-1"
                @click="goToPupil(p)"
              >
                <template #prepend>
                  <v-avatar :color="avatarColor(p.gender)" size="32">
                    <span class="avatar-initials">{{
                      initials(p.prenom, p.nom)
                    }}</span>
                  </v-avatar>
                </template>

                <v-list-item-title class="font-weight-medium">
                  {{ p.prenom }} {{ p.nom }}
                </v-list-item-title>

                <v-list-item-subtitle class="text-medium-emphasis">
                  {{ classLabel(activeClass) }}
                </v-list-item-subtitle>

                <template #append>
                  <div class="d-flex align-center" style="gap: 4px">
                    <v-btn
                      icon
                      size="small"
                      variant="text"
                      color="primary"
                      @click.stop="handleQuickNote(p)"
                      aria-label="Ajouter une note"
                    >
                      <v-icon size="20">mdi-note-plus-outline</v-icon>
                    </v-btn>
                    <v-btn
                      icon
                      size="small"
                      variant="text"
                      color="warning"
                      @click.stop="handleQuickAbsence(p)"
                      aria-label="Marquer absent"
                    >
                      <v-icon size="20">mdi-account-off-outline</v-icon>
                    </v-btn>
                    <v-btn
                      icon
                      size="small"
                      variant="text"
                      @click.stop="goToPupil(p)"
                      aria-label="Ouvrir profil"
                    >
                      <v-icon>mdi-open-in-new</v-icon>
                    </v-btn>
                  </div>
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-dialog>

      <!-- Quick Actions Dialogs -->
      <AddNoteDialog
        v-if="selectedPupil"
        v-model="quickNoteDialog"
        :pupil-id="selectedPupil.id"
        @saved="refreshPupils"
      />

      <QuickAbsenceDialog
        v-if="selectedPupil"
        v-model="quickAbsenceDialog"
        :pupils="[selectedPupil]"
        @saved="refreshPupils"
      />
    </div>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from "vue";
import { useRouter } from "vue-router";
import { api } from "../services/api";
import { useAuthStore } from "../stores/auth";
import { useSchoolStore } from "../stores/school";
import { useToastStore } from "../stores/toast";
import ClassCards from "../components/dashboard/ClassCards.vue";
import PresenceCard from "../components/dashboard/PresenceCard.vue";
import AddNoteDialog from "../components/dialogs/AddNoteDialog.vue";
import QuickAbsenceDialog from "../components/dialogs/QuickAbsenceDialog.vue";

const router = useRouter();
const auth = useAuthStore();
const school = useSchoolStore();
const toast = useToastStore();

const pupils = ref([]);
const activeClass = ref(null);
const pupilsDialog = ref(false);
const pupilQuery = ref("");
const quickNoteDialog = ref(false);
const quickAbsenceDialog = ref(false);
const selectedPupil = ref(null);
const error = ref("");
const presenceRate = ref(null);
const presenceError = ref("");

const loading = reactive({
  pupils: false,
  presence: false,
});

const displayName = computed(
  () => auth.me?.firstName || auth.me?.lastName || auth.username || "",
);

const classes = computed(() => school.classrooms);

const presenceRateDisplay = computed(() => {
  return typeof presenceRate.value === "number"
    ? Math.round(presenceRate.value)
    : null;
});

const pupilsFiltered = computed(() => {
  const list = pupils.value || [];
  const q = (pupilQuery.value || "").trim().toLowerCase();
  if (!q) return list;
  return list.filter((p) => {
    const nom = (p.nom || "").toLowerCase();
    const prenom = (p.prenom || "").toLowerCase();
    return nom.includes(q) || prenom.includes(q);
  });
});

onMounted(async () => {
  await Promise.all([
    school.fetchMyClassrooms(),
    loadPresence(),
    auth.whoAmI(),
  ]);
});

async function loadMyClasses() {
  await school.fetchMyClassrooms();
}

async function loadPresence() {
  loading.presence = true;
  presenceError.value = "";
  try {
    const { data } = await api.get("/attendance/presence-rate");
    // Si rate est null mais qu'on a des données, c'est qu'il n'y a pas d'élèves -> afficher 0
    if (data && data.rate === null && data.pupilCount === 0) {
      presenceRate.value = 0;
    } else {
      presenceRate.value = typeof data?.rate === "number" ? data.rate : null;
    }
  } catch (e) {
    console.warn("API presence-rate error:", e.message);
    presenceError.value = "Données non disponibles";
    presenceRate.value = null;
  } finally {
    loading.presence = false;
  }
}

async function openClass(c) {
  activeClass.value = c;
  pupilsDialog.value = true;
  pupilQuery.value = "";
  await loadPupils(c.id);
}

async function loadPupils(classroomId) {
  if (!classroomId) return;
  loading.pupils = true;
  try {
    const { data } = await api.get(`/classrooms/${classroomId}/list/pupils`, {
      params: { _ts: Date.now() },
    });
    pupils.value = Array.isArray(data) ? data : [];
  } catch (e) {
    toast.error("Impossible de charger les élèves.");
    pupils.value = [];
  } finally {
    loading.pupils = false;
  }
}

async function refreshPupils() {
  if (!activeClass.value) return;
  await loadPupils(activeClass.value.id);
}

function goToPupil(p) {
  router.push({ name: "pupil-profile", params: { id: p.id } });
}

function handleQuickNote(p) {
  selectedPupil.value = p;
  quickNoteDialog.value = true;
}

function handleQuickAbsence(p) {
  selectedPupil.value = p;
  quickAbsenceDialog.value = true;
}

function classLabel(c) {
  if (!c) return "";
  return c.name || c.nom || `Classe #${c.id}`;
}

function teacherLabel(c) {
  if (!c) return "Enseignant non renseigné";
  const ln = (c.teacherLastName || "").trim();
  const fn = (c.teacherFirstName || "").trim();
  const full = `${ln ? ln.toUpperCase() : ""} ${fn}`.trim();
  return full || "Enseignant non renseigné";
}

function cycleLabel(c) {
  const n = (classLabel(c) || "").toUpperCase();
  if (n.includes("CP") || n.includes("CE1") || n.includes("CE2"))
    return "Cycle 2";
  if (n.includes("CM1") || n.includes("CM2")) return "Cycle 3";
  return "Cycle";
}

function initials(firstName, lastName) {
  const f = (firstName || "").trim().charAt(0).toUpperCase();
  const l = (lastName || "").trim().charAt(0).toUpperCase();
  return f + l || "—";
}

function avatarColor(gender) {
  if (String(gender).toUpperCase() === "GIRL") return "pink";
  if (String(gender).toUpperCase() === "BOY") return "blue";
  return "grey";
}

function pickColor(c) {
  const palette = ["blue", "purple", "orange", "green", "pink"];
  return palette[Math.abs(Number(c?.id || 0)) % palette.length];
}

function pupilsCount(c) {
  return c?.pupilsCount ?? c?.pupilCount ?? c?.nbEleves ?? 0;
}

function coursesCount(c) {
  return c?.coursesCount ?? c?.nbCours ?? 12;
}
</script>

<style scoped>
.dashboard {
  padding: 32px 24px;
}

.dashboard__inner {
  max-width: 1280px;
  margin: 0 auto;
}

.avatar-initials {
  font-weight: 800;
  font-size: 12px;
}

.class-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}
</style>
