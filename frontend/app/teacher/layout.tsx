import AuthGuard from "@/components/AuthGuard";
import TeacherSidebar from "@/components/teacher/TeacherSidebar";
import TeacherTopbar from "@/components/teacher/TeacherTopbar";

export default function TeacherLayout({ children }: { children: React.ReactNode }) {
  return (
    <AuthGuard allowedRoles={["Teacher"]}>
      <div className="min-h-screen flex bg-bg">
        <TeacherSidebar />
        <div className="flex-1 flex flex-col min-w-0">
          <TeacherTopbar />
          <main className="flex-1 p-6 max-w-5xl w-full mx-auto">{children}</main>
        </div>
      </div>
    </AuthGuard>
  );
}
