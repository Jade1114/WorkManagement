Role:
Admin, Student, Teacher

Class:
学科，作业，提交记录，用户

1. admin 可以更改 student,teacher 的信息，包括角色的更改
2. admin,teacher 可以 CRUD 学科 (暂时没有学生选科功能，所以每一个学科都包含所有的学生)
3. teacher 在创建作业时，需选择对应的学科，确定作业的信息
4. student 在收到对应作业时，可以对该作业进行一次提交
5. teacher 仅可查看自己发布作业的提交，并对其进行批改，评分
6. 多教师共存，各教师数据隔离（作业/提交统计仅统计本教师）

学科，作业，提交，用户的 CRUD 都采用软删除(isDelete)
